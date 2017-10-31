/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.carbaresist.services;

import com.starkindustriesne.carbaresist.model.Job;
import com.starkindustriesne.carbaresist.model.JobResult;
import com.starkindustriesne.carbaresist.model.JobResultEntry;
import com.starkindustriesne.carbaresist.model.JobStatus;
import com.starkindustriesne.carbaresist.repositories.JobRepository;
import com.starkindustriesne.carbaresist.repositories.JobResultRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.alignment.template.GapPenalty;
import org.biojava.nbio.alignment.template.PairwiseSequenceAligner;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.sequence.transcription.TranscriptionEngine;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author mstark
 */
@Service
public class JobProcessorService {

    @Autowired
    private JobRepository jobRepo;

    @Autowired
    private JobResultRepository jobResultRepository;

    @Autowired
    private RabbitTemplate messageSender;

    @Autowired
    private String queueName;
    
    @Autowired
    private String aaSubstitutionMatrix;

    private HttpClient httpClient;

    private TranscriptionEngine transcriptionEngine;
    
    private SubstitutionMatrix<AminoAcidCompound> matrix;
    
    private GapPenalty gapPenalty;

    @PostConstruct
    public void init() {
        httpClient = HttpClientBuilder.create().build();

        TranscriptionEngine.Builder teBuilder = new TranscriptionEngine.Builder();

        teBuilder.table(11).initMet(true).trimStop(false);

        transcriptionEngine = teBuilder.build();
        
        matrix = SubstitutionMatrixHelper.getAminoAcidSubstitutionMatrix(aaSubstitutionMatrix);
        
        gapPenalty = new SimpleGapPenalty();
    }

    private Map<String, DNASequence> entrezGenomeSearch(Iterable<String> genomeIds) throws IOException {
        String url = String.format("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?"
                + "db=nuccore&id=%s&rettype=fasta&retmode=text", String.join(",", genomeIds));

        HttpGet request = new HttpGet(url);

        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        Map<String, DNASequence> sequences = null;

        try (InputStream is = entity.getContent()) {
            sequences
                    = FastaReaderHelper.readFastaDNASequence(is);
        }

        return sequences;
    }

    private Map<String, ProteinSequence> entrezProteinSearch(Iterable<String> resistanceGeneIds) throws IOException {
        String url = String.format("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?"
                + "db=protein&id=%s&rettype=fasta&retmode=text", String.join(",", resistanceGeneIds));

        HttpGet request = new HttpGet(url);

        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        Map<String, ProteinSequence> sequences = null;

        try (InputStream is = entity.getContent()) {
            sequences
                    = FastaReaderHelper.readFastaProteinSequence(is);
        }

        return sequences;
    }

    public void processJob(Job job) {
        if(job.getJobStatus() != JobStatus.SUBMITTED) {
            return;
        }
        
        job.setJobStatus(JobStatus.RUNNING);
        job = jobRepo.save(job);

        JobResult result = new JobResult();
        result.setStart(new Date());
        result.setJobId(job.getJobId());

        try {

            result = jobResultRepository.save(result);

            Map<String, DNASequence> genomes = this.entrezGenomeSearch(job.getGenomeIds());
            Map<String, ProteinSequence> processedGenomes = new HashMap<>();

            for (Map.Entry<String, DNASequence> genome : genomes.entrySet()) {
                processedGenomes.put(genome.getKey().substring(0, genome.getKey().indexOf(" ")), genome.getValue()
                        .getRNASequence().getProteinSequence(transcriptionEngine));
            }

            Map<String, ProteinSequence> resistanceGenes
                    = this.entrezProteinSearch(job.getResistanceGeneIds());
            
            Map<String, ProteinSequence> processedResistanceGenes = new HashMap<>();
            
            for (Map.Entry<String, ProteinSequence> resistanceGene
                    : resistanceGenes.entrySet()) {
                processedResistanceGenes.put(resistanceGene.getKey()
                        .substring(0, resistanceGene.getKey().indexOf(" ")),
                        resistanceGene.getValue());
            }

            for (String genomeId : job.getGenomeIds()) {
                ProteinSequence genome = processedGenomes.get(genomeId);

                for (String resistanceGeneId : job.getResistanceGeneIds()) {
                    PairwiseSequenceAligner<ProteinSequence, AminoAcidCompound> aligner = Alignments.getPairwiseAligner(genome,
                                    processedResistanceGenes.get(resistanceGeneId),
                                    PairwiseSequenceAlignerType.LOCAL, gapPenalty, matrix);
                    
                    SequencePair<ProteinSequence, AminoAcidCompound> alignment = aligner.getPair();

                    JobResultEntry entry = new JobResultEntry();

                    entry.setJobResultId(result.getJobResultId());
                    entry.setGenomeId(genomeId);
                    entry.setResistanceGeneId(resistanceGeneId);
                    entry.setAlignment(alignment.toString());
                    entry.setScore((int)(aligner.getScore() * 100));

                    result.getEntries().add(entry);
                }

                job.setJobStatus(JobStatus.DONE);
            }

            result.setEnd(new Date());
            result.setMessage("The job executed successfully.");
        } catch (NullPointerException | IOException e) {
            job.setJobStatus(JobStatus.ERROR);
            result.setMessage(e.toString());
        } finally {
            jobResultRepository.save(result);

            job = jobRepo.save(job);
        }

    }
}

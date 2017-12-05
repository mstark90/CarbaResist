/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.carbaresist.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starkindustriesne.carbaresist.model.JobResultEntry;
import com.starkindustriesne.carbaresist.model.JobTask;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
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
import org.biojava.nbio.core.sequence.RNASequence;
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

    private static final Logger logger = Logger.getLogger(JobProcessorService.class);

    @Autowired
    private RabbitTemplate messageSender;

    @Autowired
    private String finishedQueue;

    private TranscriptionEngine transcriptionEngine;

    private GapPenalty gapPenalty;

    private ObjectMapper serializer;

    @PostConstruct
    public void init() {
        TranscriptionEngine.Builder teBuilder = new TranscriptionEngine.Builder();

        teBuilder.table(11).initMet(true).trimStop(false);

        transcriptionEngine = teBuilder.build();

        gapPenalty = new SimpleGapPenalty();

        serializer = new ObjectMapper();
    }

    private InputStream getInputStream(String content) {
        return new ByteArrayInputStream(content.getBytes(Charset.forName("utf-8")));
    }

    private Map<String, DNASequence> parseDNASequence(String fasta) throws IOException {
        Map<String, DNASequence> sequences = null;

        try (InputStream is = getInputStream(fasta)) {
            sequences
                    = FastaReaderHelper.readFastaDNASequence(is);
        }

        return sequences;
    }

    private Map<String, ProteinSequence> parseProteinSequence(String fasta) throws IOException {
        Map<String, ProteinSequence> sequences = null;

        try (InputStream is = getInputStream(fasta)) {
            sequences
                    = FastaReaderHelper.readFastaProteinSequence(is);
        }

        return sequences;
    }
    
    public boolean isDNASequence(String sequence) {
        Pattern regex = Pattern.compile("^[ACGT]+$", Pattern.CASE_INSENSITIVE);
        
        return regex.matcher(sequence).matches();
    }

    public void processJobTask(String message) throws IOException {
        if (message.length() == 0) {
            return;
        }

        JobTask job = serializer.readValue(message, JobTask.class);

        logger.info(String.format("Starting task for job %s", job.getJobId()));

        JobResultEntry entry = new JobResultEntry();

        entry.setJobResultId(job.getJobResultId());

        SubstitutionMatrix<AminoAcidCompound> matrix
                = SubstitutionMatrixHelper.getAminoAcidSubstitutionMatrix(job.getSubstitutionMatrix().getName());

        try {

            Map<String, DNASequence> genomes = this.parseDNASequence(job.getGenomeFasta());
            Map<String, ProteinSequence> processedGenomes = new HashMap<>();

            for (Map.Entry<String, DNASequence> genome : genomes.entrySet()) {
                RNASequence rna = genome.getValue().getRNASequence();
                
                entry.setGenomeName(genome.getKey());
                entry.setGenomeId(genome.getKey().substring(0, genome.getKey().indexOf(" ")));
                
                processedGenomes.put(genome.getKey().substring(0, genome.getKey().indexOf(" ")),
                        rna.getProteinSequence(transcriptionEngine));
            }

            String resistanceFasta = job.getResistanceGeneFasta();

            Map<String, ProteinSequence> processedResistanceGenes = new HashMap<>();

            if (isDNASequence(resistanceFasta.substring(resistanceFasta.indexOf("\n") + 1))) {
                Map<String, DNASequence> resistanceGenes
                        = parseDNASequence(job.getResistanceGeneFasta());
                
                for (Map.Entry<String, DNASequence> resistanceGene
                        : resistanceGenes.entrySet()) {
                    entry.setResistanceGeneName(resistanceGene.getKey());
                    entry.setResistanceGeneId(resistanceGene.getKey()
                            .substring(0, resistanceGene.getKey().indexOf(" ")));
                    
                    processedResistanceGenes.put(resistanceGene.getKey()
                            .substring(0, resistanceGene.getKey().indexOf(" ")),
                            resistanceGene.getValue().getRNASequence()
                                    .getProteinSequence(transcriptionEngine));
                }
            } else {
                Map<String, ProteinSequence> resistanceGenes
                        = parseProteinSequence(job.getResistanceGeneFasta());
                
                for (Map.Entry<String, ProteinSequence> resistanceGene
                        : resistanceGenes.entrySet()) {
                    entry.setResistanceGeneName(resistanceGene.getKey());
                    entry.setResistanceGeneId(resistanceGene.getKey()
                            .substring(0, resistanceGene.getKey().indexOf(" ")));
                    
                    processedResistanceGenes.put(resistanceGene.getKey()
                            .substring(0, resistanceGene.getKey().indexOf(" ")),
                            resistanceGene.getValue());
                }
            }

            for (String genomeId : processedGenomes.keySet()) {
                ProteinSequence genome = processedGenomes.get(genomeId);

                for (String resistanceGeneId : processedResistanceGenes.keySet()) {
                    PairwiseSequenceAligner<ProteinSequence, AminoAcidCompound> aligner = Alignments.getPairwiseAligner(genome,
                            processedResistanceGenes.get(resistanceGeneId),
                            PairwiseSequenceAlignerType.LOCAL, gapPenalty, matrix);

                    SequencePair<ProteinSequence, AminoAcidCompound> alignment = aligner.getPair();
                    
                    String alignmentStr = "CLUSTAL\n%s\t%s\n%s\t%s";
                    
                    String[] alignmentParts = alignment.toString().split("\n");

                    entry.setAlignment(String.format(alignmentStr, genomeId, alignmentParts[0], resistanceGeneId, alignmentParts[1]));
                    entry.setScore((int) (aligner.getScore()));

                }
            }
            entry.setMessage("The entry was processed successfully.");
        } catch (Exception e) {
            logger.warn("Could not process the genome: ", e);

            entry.setMessage(e.toString());
        }

        messageSender.convertAndSend(finishedQueue, serializer.writeValueAsString(entry));

        logger.info(String.format("Finished task for job %s", job.getJobId()));
    }
}

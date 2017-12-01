//
//  main.cpp
//  CarbaResist_Process_Native
//
//  Created by Michael Stark on 11/28/17.
//  Copyright © 2017 Michael Stark. All rights reserved.
//

#include <iostream>
#include <fstream>

#include <seqan/align.h>
#include <seqan/seq_io.h>
#include <seqan/sequence.h>
#include <seqan/graph_msa.h>

using namespace seqan;

int main(int argc, const char * argv[]) {
    if(argc < 5) {
        std::cout << "Usage: CarbaResist_Process_Native <TASK_ID> <MATRIX> <FASTA1> <FASTA2>" << std::endl;
    } else {
        std::string taskId = argv[1];
        int matrix = atoi(argv[2]);
        const char* fasta1 = argv[3];
        const char* fasta2 = argv[4];
        
        seqan::CharString id1, id2;
        seqan::String<AminoAcid> sequence1, sequence2;
        
        seqan::SeqFileIn reader1(fasta1);
        seqan::SeqFileIn reader2(fasta2);
        
        seqan::readRecord(id1, sequence1, reader1);
        seqan::readRecord(id2, sequence2, reader2);
        
        Align<String<AminoAcid> > align;
        
        resize(rows(align), 2);
        
        assignSource(row(align, 0), sequence1);
        assignSource(row(align, 1), sequence2);
        
        switch(matrix) {
            case 0:
                globalMsaAlignment(align, Blosum80());
                break;
            case 1:
                globalMsaAlignment(align, Blosum62());
                break;
            case 2:
                globalMsaAlignment(align, Blosum45());
                break;
            case 3:
                globalMsaAlignment(align, Blosum30());
                break;
            case 4:
                globalMsaAlignment(align, Pam40());
                break;
            case 5:
                globalMsaAlignment(align, Pam120());
                break;
            case 6:
                globalMsaAlignment(align, Pam200());
                break;
            case 7:
                globalMsaAlignment(align, Pam250());
                break;
        }
        
        std::fstream output(taskId.append(".out"), std::ios::binary | std::ios::out);
        
        output << align << std::endl;
    }
    
    return 0;
}

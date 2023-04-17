#!/bin/bash

# specify the directory containing the files
dir=$1

# specify the command-line tool to run
tool=./src/model/mmseqs/bin/mmseqs

# loop over every file in the directory
for file in "$dir"/*.fa*; do
  # run the tool on the file
  $tool easy-cluster "$file" "$file" "$1"/tmp --min-seq-id 0.95 -c 0.95
done

# clean
rm -r $1/tmp
rm $1/*_cluster.tsv
rm $1/*_all_seqs.fasta

# move pre-processed data to a new folder
mkdir $1/preProcessed

for file in $1/*_rep_seq.fasta; do
    mv "$file" "$1/preProcessed/"
done

# rename the files
for file in $1/preProcessed/*; do
     mv "$file" "${file%_rep_seq.fasta}"
done


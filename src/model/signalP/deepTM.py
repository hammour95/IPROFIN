import biolib
import argparse

__author__ = "Majd Hammour"

def main():
    """
        DeepTMHMM launcher

        Usage: deepTM -i <FastA_File>

        Options:
            -h,  --help                     show this help message and exit
            -i,  --input                    Path to FastA file
        """

    parser = argparse.ArgumentParser(description='DeepTMHMM launcher',
                                     epilog="Author(s): " + __author__)

    parser.add_argument('-i', '--input', type=str, required=True, action="store", dest="i",
                        help="Path to FastA file")

    options = parser.parse_args()

    file = "--fasta " + options.i

    deeptmhmm = biolib.load('DTU/DeepTMHMM')

    deeptmhmm_job = deeptmhmm.cli(args=file)
    deeptmhmm_job.save_files('Results')


if __name__ == '__main__':

    main()

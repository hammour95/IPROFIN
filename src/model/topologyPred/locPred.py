import os
import re

import pandas as pd
import numpy as np
from Bio import SeqIO
from Bio.SeqUtils.ProtParam import ProteinAnalysis
from joblib import dump, load

from sklearn import svm
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.pipeline import Pipeline
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, \
    classification_report


import argparse

__author__ = "Majd Hammour"

from sklearn.preprocessing import StandardScaler
from sklearn.svm import SVC

aminoAcids = ['A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'Y']
dipeptides = [aa1 + aa2 for aa1 in aminoAcids for aa2 in aminoAcids]
tripeptides = [aa1 + aa2 + aa3 for aa1 in aminoAcids for aa2 in aminoAcids for aa3 in aminoAcids]
ompMotifs = ['AAGAAG', 'AAGKIS', 'AALAAN', 'AANANI', 'AASAVE', 'AASTTA', 'AAYRYS', 'ADAADR', 'ADLFPR', 'AEIREK',
          'AELEQQ', 'AETLAE', 'AGAGAE', 'AGARYI', 'AGGAIF', 'AGLAAL', 'AGLGAA', 'AGQASA', 'AGSGQV', 'AGTVTT',
          'AKVTIT', 'ALAAPL', 'ALAAVL', 'ALALLA', 'ALAQQA', 'ALASQA', 'ALAVTT', 'ALGALG', 'ALGGGW', 'ALKVKR',
          'ALLPSA', 'ALLVAG', 'ALQEFG', 'ANAAEI', 'APAQAE', 'AQAAVE', 'AQTLEQ', 'ARIEVG', 'ASAREG', 'ASNGLR',
          'LGRLGL', 'ATGAAV', 'ATLGLV', 'ATLTLT', 'AVAVAL', 'AVDFHG', 'AVDVAR', 'AVIAEV', 'CFCLPL', 'DGQDGD',
          'DGTLNL', 'DIQEFI', 'DIRVDG', 'DNSKTD', 'DPRVKG', 'DRWQST', 'DSVPLL', 'DTLVVT', 'DYGSLS', 'EAYLAL',
          'EELGDL', 'EFLDRL', 'EGINKV', 'ELAQAN', 'ELDLFG', 'ELGGKR', 'ELSLWI', 'EQGLEN', 'ESLGLR', 'ESRRAL',
          'FGDSLS', 'FGRSKD', 'FKLNYA', 'FMGWMW', 'FRDFAE', 'FSLKNS', 'FTGKGY', 'FVSLNA', 'GASAGV', 'GASSGY',
          'GDGGAI', 'GDSLSD', 'GELSLS', 'GFIEDS', 'GFNLNY', 'GFSSRD', 'GGAISS', 'GGAIYA', 'GGANAA', 'GGGAIY',
          'GGKGGA', 'GGKRGA', 'GGRLRA', 'GGVVNI', 'GGVWGR', 'GKGGAI', 'GLGSAA', 'GPFVIN', 'GQTVVI', 'GSFDYG',
          'GSGALG', 'GSGGSL', 'GSGQLS', 'GTILFS', 'GTLSGK', 'GTLSSA', 'GTLTVS', 'GTVSGL', 'GVGINL', 'GVKTDL',
          'GVLKTD', 'GYFDFR', 'HRIATL', 'IDNTST', 'IEARIV', 'IEQGTV', 'IGAARA', 'IGRAGL', 'IGVLTD', 'ISLTAN',
          'ISSPRL', 'IYRNSP', 'KEVLRD', 'KGGAIY', 'KINEGP', 'KITINN', 'KLSADE', 'KTLFTK', 'KVPFLG', 'LAAAVA',
          'LAEPNL', 'LAFAGL', 'LALGGL', 'LALSIS', 'LAPAQA', 'LATASL', 'LAVAVA', 'LDKQFF', 'LDLELS', 'LDVLDA',
          'LFSLLE', 'LGAATA', 'LGALFR', 'LGDIPV', 'LGGDGI', 'LGNLFK', 'LGRLGL', 'LGTYLT', 'LIACLS', 'LIDGKP',
          'LLAATP', 'LLDAQR', 'LLDVLD', 'LNLSIP', 'LPIFTA', 'LRPGMT', 'LSAGVS', 'LSERRA', 'LSISGN', 'LSLLPL',
          'LTLDPD', 'LTQPLF', 'LTVTDT', 'LVAKAD', 'LVDGVR', 'LVVDLS', 'MKKLLP', 'MKKTLL', 'NAAFSN', 'NALSKR',
          'NAQLSL', 'NATLNG', 'NEVTGL', 'NGTVNI', 'NISRNF', 'NNGAIL', 'NNGTLI', 'NNNINA', 'NQLSVS', 'NRSTLS',
          'NSIYID', 'NTKTSS', 'NTTINS', 'NVTLQG', 'NYAAGG', 'PGVSVG', 'PLGLSD', 'PLLGDI', 'PTLDLT', 'PVLAAD',
          'PVQVLA', 'QANAAT', 'QASWLA', 'QFYLGA', 'QGTVTL', 'QLGGDI', 'QPLFDY', 'QSSSAA', 'QTDDET', 'RAALLP',
          'RADLFP', 'RALALA', 'RDFAEN', 'RGPEGR', 'RLNALE', 'RLNQLS', 'RLVSLV', 'RVEILR', 'SAGSLA', 'SALALA',
          'SASRTV', 'SFLPSV', 'SGLGRA', 'SGQTYN', 'SGSFNF', 'SGSSSS', 'SLAGTV', 'SLIALA', 'SLLAGS', 'SLLALS',
          'SLLDVL', 'SLLIGG', 'SLQQPL', 'SLSLPP', 'SNITGG', 'SQLDWK', 'SRFSTS', 'SRLTLG', 'SRPVAD', 'SSSSSSSS',
          'STVVEL', 'SVNIRG', 'SVNVVG', 'TADGQL', 'TAPVFA', 'TASLLA', 'TATDLG', 'TDTPAV', 'TFYTKL', 'TGAGTL',
          'TGDIGN', 'TGTLNI', 'TITGNK', 'TLGDGY', 'TLSGKT', 'TLSSAG', 'TMVVTA', 'TTLSAG', 'TVSLSG', 'TVVSAP',
          'VAAALV', 'VAQRTA', 'VASELA', 'VATYRN', 'VDGSLS', 'VDGVLK', 'VGDSSK', 'VGVAFG', 'VGVTAK', 'VIQNSG',
          'VLDAQR', 'VNNLFD', 'VPAGPF', 'VPGLTF', 'VPLLGD', 'VPPGPF', 'VPVAQV', 'VPWDQA', 'VRLDGG', 'VRLVVA',
          'VRYDEA', 'VSGPPR', 'VSGRFD', 'VSPSSE', 'VSSGGT', 'VSVVTS', 'YAERGL', 'YQDGSA', 'YTVLDQ', 'YTVRGF']
motifs = [['Extracellular', '(GG.G.D.*){4}'],
          ['Periplasmic', 'G[FYIL][DE][LIVMT][DE][LIVMF]...[LIVMA][VAGC]..[LIVMAGN]'],
          ['Periplasmic', '[GAP][LIVMFA][STAVDN]....[GSAV][LIVMFY]{2}Y[ND]...[LIVMF].[KNDE]'],
          ['Periplasmic', '[AG].{6,7}[DNEG]..[STAIVE][LIVMFYWA].[LIVMFY].[LIVM][KR][KRHDE][GDN][LIVMA][KNGSP][FW]'],
          ['Periplasmic', 'GSYPSGHT'], ['Periplasmic', '[LIVMFY][APN].[DNS][KREQ]E[STR][LIVMAR].[FYWT].[NC][LIVM]..[LIVM]P[PAS]'],
          ['OuterMembrane', '[LIVMFY]..G..Y.F.K..[SN][STAV][LIVMFYW]V'],
          ['OuterMembrane', '(G[LIVMFY]N[LIVM]KYRYE)'],
          ['OuterMembrane', '([FYW]..G.GY[KR]F)'],
          ['OuterMembrane', '[VL][PASQ][PAS]G[PAD][FY].[LI][DNQSTAP][DNH][LIVMFY]'],
          ['OuterMembrane', '[GR][DEQKG][STVM][LIVMA]{3}[GA]G[LIVMFY].{11}[LIVM]P[LIVMFYWGS][LIVMF][GSAE].[LIVM]P[LIVMFYW]{2}..[LV]F'],
          ['OuterMembrane', '((WTD.S.HP.T).*(AGYQE[ST]R[FYW]S[FYW][TN]A.GG[ST]Y))|((AGYQE[ST]R[FYW]S[FYW][TN]A.GG[ST]Y).*(WTD.S.HP.T))'],
          ['OuterMembrane', '[LIVMA].[GT].[TA][DA]..[DG][GSTP]..[LFYDE][NQS]..[LI][SG][QE][KRQE]RA..[LV]...[LIVMF].{4,5}[LIVM]....[LIVM]...[SG].G'],
          ['Periplasmic', '[GA][IMFAT]H[LIVF]H.{2}[GP][SDG].[STAGDE]'], ['Periplasmic', '[IV].DS[GAS][GASC][GAST][GA]T'],
          ['Periplasmic', 'G[GN][SGA]G.R.[SGA]C.{2}[IV]'],
          ['Periplasmic', 'K.[NQEK][GT]G[DQ].[LIVM].{3}QS'],
          ['CytoplasmicMembrane', 'RTE[EQ]Q.{2}[SA][LIVM].[EQ]TAASMEQLTATV'],
          ['Periplasmic', '[LIVMA]{4}C[LIVMFA]T[LIVMA]{2}.{4}[LIVM].[RG].{2}L[CY]'],
          ['CytoplasmicMembrane', '[GST][LIVMF][LIVMFCA].[LIVMF][GSA][LIVM].P[LIVMFY]{2}.[AS][GSTQ][LIVMFAT]{3}[EQ][LIVMFA]{2}'],
          ['CytoplasmicMembrane', '[LIVMFYW]{2}.[DE].[LIVM][STDNQ].{2,3}[GK][LIVMF][GST][NST]G.[GST][LIV][LIVFP]'],
          ['Periplasmic', 'NPK[ST]SG.AR'],
          ['Periplasmic', '[LIVFAG].[GASV][LIVFA].[IV]H.{3}[LIVM][GSTAE][STANH].{1,3}[STN]W[LIVMFYW]'],
          ['Periplasmic', '[EQ].{4}H.{5}[GSTA].{3}[FY].{3}[AG].{2}[AV]H.{7}P'],
          ['CytoplasmicMembrane', '[DENQ]...G[FYWMQ].[LIVMF]R..H'],
          ['CytoplasmicMembrane', 'P[DE]W[FY][LFY]{2}'],
          ['CytoplasmicMembrane', '[GSDN]WT[LIVM].[FY]W.WW'],
          ['CytoplasmicMembrane', '[STAGN].[STAG][LIVMF]RL.[SAGV]N[LIVMT]'],
          ['CytoplasmicMembrane', 'P.{0,1}G[DE].[LIVMF]{2}.[LIVM]{2}[KREQ][LIVM]{3}.P'],
          ['CytoplasmicMembrane', 'P.G.[STA].[NT][LIVMC]DG[STAN].[LIVM][FY].{2}[LIVM].{2}[LIVM][FY][LI][SA]Q'],
          ['CytoplasmicMembrane', '[LIVMF].G[LIVMFA]..G.{8}[LIFY]..[EQ].{6}[RK]'],
          ['CytoplasmicMembrane', '[STAGC]G[PAG].{2,3}[LIVMFYWA]{2}.[LIVMFYW].[LIVMFWSTAGC]{2}[STAGC]...[LIVMFYWT].[LIVMST]...[LIVMCTA][GA]E.{5}[PSAL]'],
          ['Extracellular', '[KT]..NW..T[DN]T'],
          ['CytoplasmicMembrane', 'N...[DEH]..[LIMF]D..[VM].R[ST]..R.{4}G'],
          ['CytoplasmicMembrane', '[YWG][LIVFYWTA]{2}[VGS]H[LNP].V.{44,47}HH'],
          ['CytoplasmicMembrane', '[HNQA].NP[STA][LIVMF][ST][LIVMF][GSTAFY]'],
          ['CytoplasmicMembrane', '[NV].{5}[GTR][LIVMA].P[PTLIVM].G[LIVM]...[LIVMFW][LIVMFW]S[YSA]GG[STN][SA]'],
          ['CytoplasmicMembrane', 'R[LIVM][GSA]EV[GSA]ARF[STAIV]LD[GSA][LM]PGKQM[GSA]ID[GSA][DA]'],
          ['CytoplasmicMembrane', 'G[LIVM]{2}.D[RK]LGL[RK]{2}.[LIVM]{2}W'],
          ['CytoplasmicMembrane', 'P.[LIVMF]{2}NR[LIVM]G.KN[STA][LIVM]{3}'],
          ['CytoplasmicMembrane', '[QEK][RF]G.{3}[GSA][LIVF][WL][NS].[SA][HM]N[LIV][GA]G'],
          ['CytoplasmicMembrane', 'A[LMF].[GAT]T[LIVMF].G.[LIVMF].{7}P'],
          ['CytoplasmicMembrane', 'IG[GA]GM[LF][SA].P.{3}[SA]G.{2}F'],
          ['Extracellular', 'D.[LI].{4}G.D.[LI].GG.{3}D']]


def main():
    """
            protein subcellular localization predictor

            Usage: locPred -m <Mode> -i input [Options]

            Options:
                -h,  --help                     show this help message and exit
                -i,  --input                    SEQUENCE OR Path to FastA files
                -m,  --mode                     predict= 1, Train= 2 OR TestAndTrain= 3
                -K,  --kernel                   Specifies the kernel type to be used in the algorithm
                -C                              Regularization parameter
                -G,                             Kernel coefficient
            """

    parser = argparse.ArgumentParser(description='protein subcellular localization predictor',
                                     epilog="Author(s): " + __author__)

    parser.add_argument('-m', '--mode', type=int, required=True, action="store", dest="m",
                        help="predict= 1, Train= 2 OR TestAndTrain= 3 ")

    parser.add_argument('-i', '--input', type=str, required=True, action="store", dest="i",
                        help="SEQUENCE OR Path to FastA files")

    parser.add_argument('-K', '--kernel', type=str, required=False, action="store", dest="K", default='linear',
                        help="Specifies the kernel type to be used in the algorithm")

    parser.add_argument('-C', type=float, required=False, action="store", dest="C", default=1,
                        help="Regularization parameter")

    parser.add_argument('-G', type=float, required=False, action="store", dest="G", default=0.1,
                        help="Kernel coefficient")

    options = parser.parse_args()

    mode = options.m
    sequence = options.i
    kernel = options.K
    C = options.C
    gamma = options.G

    # predict Mode
    if mode == 1:
        if os.path.isfile("clf.joblib"):
            predict(sequence)

    # Learn and dump CLF
    elif mode == 2:
        data = importDataset(sequence)
        extractFeatures(data)
        trainAndDump("features_data.csv", kernel, C, gamma, 42)

    # Test CLF Find optimal parameter
    elif mode == 3:
        data = importDataset(sequence)
        extractFeatures(data)
        learn("features_data.csv")


def importDataset(directory):
    """
    import data from fasta files in a directory
    :param directory: Path to directory contains fasta files
    :return: pandas dataframe contains label and sequence columns
    """
    records = []

    for filename in os.listdir(directory):
        if filename.endswith(".fasta"):
            file_path = os.path.join(directory, filename)
            label = os.path.splitext(filename)[0]

            for record in SeqIO.parse(file_path, 'fasta'):
                records.append((label, str(record.seq)))

    data = pd.DataFrame.from_records(records, columns=['Label', 'Sequence'])

    return data


def extractFeatures(data):
    """
    extract the features from the data (proteins):
        amino acid composition
        physicochemical properties
        secondary structure
        di-peptide composition
        and OMP motifs
    :param data: pandas dataframe contains label and sequence columns
    :return: save the dataset as csv file to the disk
    """
    features = []
    for sequence in data['Sequence']:
        # length
        length = len(sequence)

        # amino acid composition
        composition = ProteinAnalysis(sequence).get_amino_acids_percent()

        # physicochemical properties
        protein = ProteinAnalysis(sequence)
        aromatic = protein.aromaticity()
        ss = protein.secondary_structure_fraction()
        pI = protein.isoelectric_point()
        chargeAtpH7 = protein.charge_at_pH(7.0)

        # motifs counter
        OMPmotif = 0
        PeriMotif = 0
        CytoMemMotif = 0
        ExtraMotif = 0

        for mo in ompMotifs:
            if mo in sequence:
                OMPmotif += 1

        for loc, regex in motifs:
            matches = re.findall(regex, sequence)
            count = len(matches)
            if loc == "Extracellular":
                ExtraMotif += count
            elif loc == "Periplasmic":
                PeriMotif += count
            elif loc == "OuterMembrane":
                OMPmotif += count
            elif loc == "CytoplasmicMembrane":
                CytoMemMotif += count

        # di-peptide composition
        diPep = computeDiPeptideComposition(sequence)

        # combine the features into a single array
        feature = np.concatenate([np.array(list(composition.values())),
                                  np.array([aromatic, pI, chargeAtpH7]), np.array(diPep),
                                  np.array(computeTriPeptideComposition(sequence)),
                                  np.array([ss[0], ss[1], ss[2]]),
                                  np.array([OMPmotif, PeriMotif, CytoMemMotif, ExtraMotif])])
        features.append(feature)

        # convert the list of features to a DataFrame
        feature_names = aminoAcids + \
                        ['aromaticity', 'Isoelectric Point', 'Charge at pH 7.0'] + \
                        dipeptides + tripeptides + \
                        ['Helix', 'Turn', 'Sheet'] + \
                        ['OMPmotif', 'PeriMotif', 'CytoMemMotif', 'ExtraMotif']

    features = pd.DataFrame(features, columns=feature_names)

    # combine the labels and features into a single DataFrame
    data = pd.concat([data['Label'], features], axis=1)

    # save the data as a new CSV file
    data.to_csv('features_data.csv', index=False)


def computeDiPeptideComposition(sequence):
    """
    Computes the di-peptides compositions in a protein sequence
    :param sequence: String     Protein sequence
    :return: Normalized di-peptide composition
    """
    # Convert the protein sequence to uppercase
    sequence = sequence.upper()

    # Compute the frequency of each dipeptide in the sequence
    composition = []
    for dipeptide in dipeptides:
        count = sequence.count(dipeptide)
        composition.append(count)

    # Normalize the composition to sum to 1
    composition = np.array(composition) / sum(composition)

    return composition


def computeTriPeptideComposition(sequence):
    """
    Computes the tri-peptides compositions in a protein sequence
    :param sequence: String     Protein sequence
    :return: Normalized tri-peptide composition
    """
    # Convert the protein sequence to uppercase
    sequence = sequence.upper()

    # Compute the frequency of each tripeptide in the sequence
    composition = []
    for tripeptide in tripeptides:
        count = sequence.count(tripeptide)
        composition.append(count)

    # Normalize the composition to sum to 1
    composition = np.array(composition) / sum(composition)

    return composition


def learn(dataset):
    """
    load and split the data to train the classifier and found the best parameter
    :param dataset: CSV file contain the Training dataset with the features
    :return: Print out the best parameters and some statistics
    """
    # load the dataset of protein sequences
    data = pd.read_csv(dataset)

    # preprocess the data by extracting relevant features
    X = data.iloc[:, 1:].values
    y = data.iloc[:, 0].values

    # split the data into training and testing sets
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    svc = Pipeline([
        ("scaler", StandardScaler()),
        ("svm", SVC())
    ])

    # Define the hyperparameter grid with multiple kernels and hyperparameters
    param_grid = {
        "svm__C": [0.1, 1, 10],
        "svm__kernel": ["linear", "rbf", "poly", "sigmoid"],
        "svm__degree": [2, 3, 4],
        "svm__coef0": [0, 1, 2],
        "svm__gamma": ["scale", "auto"],
    }

    # Perform grid search
    grid = GridSearchCV(svc, param_grid=param_grid, cv=5, n_jobs=-1)

    # fit the grid search to the training data
    grid.fit(X_train, y_train)

    # get the best model from the grid search
    best_model = grid.best_estimator_

    # Save the trained SVM for later use
    dump(best_model, 'clf.joblib')

    # make predictions on the testing set using the best model
    y_pred = best_model.predict(X_test)

    # evaluate the performance of the model using various metrics
    accuracy = accuracy_score(y_test, y_pred)
    precision = precision_score(y_test, y_pred, average='weighted')
    recall = recall_score(y_test, y_pred, average='weighted')
    f1 = f1_score(y_test, y_pred, average='weighted')

    print('Best hyperparameters:', grid.best_params_)
    print('Accuracy:', accuracy)
    print('Precision:', precision)
    print('Recall:', recall)
    print('F1-score:', f1)

    report = classification_report(y_test, y_pred)

    print(report)


def trainAndDump(dataset, kernel='linear', C=1, gamma=0.1, random_state=42):
    """
    Train the classifier and dump it to use again
    :param dataset: CSV file contain the Training dataset with the features
    :param kernel: Specifies the kernel type to be used in the algorithm
    :param C: Regularization parameter
    :param gamma: Kernel coefficient
    :param random_state: random number generation for shuffling the data
    :return: saves the classifier to the disk
    """
    # load the dataset of protein sequences
    trainData = pd.read_csv(dataset)

    # preprocess the data by extracting relevant features
    X = trainData.iloc[:, 1:].values
    y = trainData.iloc[:, 0].values

    # train an SVM model on the training set
    clf = svm.SVC(kernel=kernel, C=C, gamma=gamma, random_state=random_state)
    clf.fit(X, y)

    dump(clf, 'clf.joblib')


def predict(filePath):
    """
    Load the classifier and make the prediction
    :param filePath: fasta File contains the sequences
    :return: print the prediction
    """
    clf = load('clf.joblib')
    records = []
    for record in SeqIO.parse(filePath, 'fasta'):
        records.append((str(record.name), str(record.seq)))
    query = pd.DataFrame.from_records(records, columns=['Label', 'Sequence'])
    extractFeatures(query)
    data = pd.read_csv("features_data.csv")
    X1 = data.iloc[:, 1:].values
    y1 = data.iloc[:, 0].values
    y_pred = clf.predict(X1)

    # combine the headers and predictions into a single DataFrame
    results = np.vstack([y1, np.array(y_pred)])
    results = pd.DataFrame(results, index=["Name", "Prediction"]).T
    # save the data as a new CSV file
    results.to_csv('locPredResults.csv', index=False, sep="\t")


if __name__ == "__main__":
    # data = importDataset("psortBTrainingDataset")
    # extractFeatures(data)
    # learn("features_data.csv")
    # trainAndDump("features_data.csv", 'linear', 10, 0.1, 42)
    # predict("test.fasta")
    main()


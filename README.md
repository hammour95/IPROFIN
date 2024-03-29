# IPROFIN

`Contact:   m.hammour@hotmail.com`

---

## **Description**:

IPROFIN is a powerful pipeline for predicting conserved outer membrane protein (OMPs) targets from a set of input sequences.
The tool combines various tools and machine learning methods, including data preprocessing, clustering of homologous proteins, pre-filtering for performance improvement, localization prediction for identifying OMPs, and multiple sequence alignment to identify conserved regions.

The tool is designed to be user-friendly and efficient, with a streamlined pipeline that reduces processing time and increases accuracy. It also includes optional tools for extra analysis and visualization, making it a versatile tool for a range of use cases.

Whether you're a bioinformatics researcher, a scientist, or a student, IPROFIN is an essential tool for analyzing protein of different species and gaining insights into the presence of OMPs and conserved regions.

## **Dependencies and Building**:

- JDK 19+
- JavaFX 19

- Download IPROFIN.zip from the releases. 

- Download The trained SVM model for the localization prediction form:  
https://1drv.ms/f/s!AmL9JchooXjA6BWxH5hLz4400hGX?e=hr0fBZ 
copy it to the main folder. 

- Download the model for SignalP6 from the link: https://mega.nz/file/uZ0DVQ7b#oDDerv_8mJmj1ypC_CX8OaWBxfYe4dXDvHGVgOiwCks
and copy it to "./IPROFIN/src/model/signalP/venv/lib/python3.9/site-packages/signalp/model_weights/".

- (TMP!) Load now the project in IntelliJ and add the VM options to the main class IProFin.java "--module-path PATH_TO/javafx-sdk-19/lib --add-modules=javafx.swing,javafx.graphics,javafx.fxml,javafx.media,javafx.web"


## Used Tools

| Name         | Version       |
|--------------|---------------|
| BioLib       | 1.1.785       |
| BioPython    | 1.79          |
| Clustal      | 1.2.3         |
| DIAMOND      | 2.0.15.153    |
| JalView      | 2.11.2.5      |
| JavaFX       | 19            |
| JobLib       | 1.1.0         |
| MatplotLib   | 3.5.2         |
| MMseqs2      | v7e2840992948ee89dcc336522dc98a74fe0adf00 |
| MSAViewer    | 1.8.2         |
| Numpy        | 1.23.2        |
| OpenJDK      | 19.0.2        |
| Pandas       | 1.5.1         |
| PyCharm      | 2022.3        |
| Python       | 3.9           |
| SciKit-Learn | 1.1.2         |
| SignalP      | 6.0           |

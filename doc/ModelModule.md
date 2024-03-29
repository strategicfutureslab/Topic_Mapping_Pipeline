# Topic Mapping Pipeline [![CC BY-NC 4.0][cc-by-nc-shield]][cc-by-nc]

[< Previous](LemmatiseModule.md) | [Index](index.md) | [Next >](InferenceModule.md)

---

# Topic Model Modules

The Topic Model modules use the lemmas data [previously created](LemmatiseModule.md) to create one or more topic 
model(s) from it. The lemmas data is then saved in a ***Document JSON file*** and the generated topics are saved in 
***Topic JSON file(s)***.

The Topic Model modules are contained in the `P3_TopicModelling` package.

## List of Topic Model Modules

There are two Topic Model modules:
- ***Topic Modelling*** (`TopicModelling.java` class), which samples a single model from the lemmas;
- ***Hierarchical Topic Modelling*** (`HierarchicalTopicModelling.java` class), which samples two separate topic models
(using the Topic Modelling module), a main model and a sub model, and creates an assignment between the two to create 
a two-layers hierarchical model.

## Specifications

The Topic Model module entry in the project file should has the following structure:
```json5
{...
  "model": {
    "lemmas": "path",
    "modelType": "module name",
    "outputDir" | "dataDir": "path",
    "documentOutput": "path",
    "model" | "mainModel": { ... },
    "subModel": { ... },
    "hierarchy": { ... }
  }
...}
```

| Name | Description | Optional | Default |
| --- | --- | --- | --- |
| `lemmas` | Path to the lemmatised documents file * | No | |
| `outputDir` or `dataDir` | Path to the directory where all files generated by the module will be saved * | Yes | `""` |
| `modelType` | Which module to use `simple` or `hierarchical` ** | No | |
| `documentOutput` | Path to the output document JSON file *** | No | |
| `model` or `mainModel` (if the model is hierarchical) | Specification object for the (main) topic model | No | |
| `subModel` | Specification object for the sub topic model | Required if `modelType` is `hierarchical` | |
| `hierarchy` | Specification object for the hierarchical assignment between main topic model and sub topic model | Required if `modelType` is `hierarchical` | |
- \* These paths are relative to the [data directory](MetaParameters.md);
- \** This gets overwritten by the [model type meta-parameter](MetaParameters.md) (if set);
- \*** This path is relative to the `outputDir` directory.

The specifiations for `mainModel` (or `model`) and `subModel` follow the same structure:
```json5
{...
  "model":{...
    "mainModel": {
      "topics": 10,
      "words": 20,
      "docs": 30,
      "iterations": 2000,
      "iterationsMax": 10,
      "topicOuput": "path",
      "serialise": "path",
      // Advanced options
      "topicSimOutput": "path",
      "numWordId": 3,
      "llOutput": "path",
      "topicLogOutput": "path",
      "alphaSum": 1.0,
      "symmetricAlpha": false,
      "beta": 0.01,
      "optimInterval": 50,
      "seed" : 0,
      "wordDistances": false
    },
  ...}
...}
```

| Name | Description | Optional | Default |
| --- | --- | --- | --- |
| `topics` | Number of topics to generate | No | |
| `words` | Number of top words to save per topic | Yes | `20`|
| `documents` | Number of top documents to save per topic | Yes | `20` |
| `iterations` | Number of sampling iterations to perform | Yes | `1000` |
| `iterationsMax` | Number of maximisation iterations to perform | Yes | `0` |
| `topicOutput` | Path to the output topic JSON file * | No | |
| `serialise` | Path to the output serialised model object * ** | Yes | `""` (no serialisation) |
- \* These paths are relative to the `outputDir` directory;
- \** Serialisation is necessary to later [infer documents](InferenceModule.md).

There are also advanced specifications:

| Name | Description | Optional | Default |
| --- | --- | --- | --- |
| `topicSimOuput` | Path to the CSV file exporting the topic similarity matrix * | Yes | `""` (no export) |
| `numWordId` | Number of top words to use to identify topics in `topicSimOutput` | Yes | `3`|
| `llOutput` | Path to the JSON file exporting the model's log-likelihood logs * | Yes | `""` (no export) |
| `topicLogOutput` | Path to the JSON file exporting the model's topic logs * | Yes | `""` (no export) |
| `alphaSum` | Sum of topics' alpha values (document to topics distribution Dirichlet prior) | Yes | `1.0` |
| `symmetricAlpha` | Symmetry of the alpha values during optimisation | Yes | `false` (no symmetry) |
| `beta` | Words beta values (topic to words distribution Dirichlet prior) | Yes | `0.01` |
| `optimInterval` | Interval (in number of iterations) between alpha and beta values optimisations | Yes | `50` |
| `seed` | Index of a random seed to use ** | Yes | `0` |
| `wordDistances` | Computation of the word distribution distances between documents and topics *** | Yes | `false` (no computation) |
- \* These paths are relative to the `outputDir` directory;
- \** There are 100 seeds available, `seed` must therefore be set between `0` and `99` (included);
- \*** If set to `true`, the word distances between documents and topics will be saved in the `documentOutput` JSON file.

The specification for `hierarchy` should follow this structure:
```json5
{...
  "model":{...
    "hierarchy": {
      "assignmentType" :  "Perceptual",
      "maxAssign": 1,
      "modelSimOutput": "path",
      "assignmentOutput": "path"
    },
  ...}
...}
```

| Name | Description | Optional | Default |
| --- | --- | --- | --- |
| `assignmentType` | Type of similarity to use for assigning sub topics to main topics: `Perceptual` based on top words overlap, `Document` based on document distributions | Yes | `Perceptual` |
| `maxAssign` | Maximum number of times a sub topics gets assigned to a main topic | Yes | `1` |
| `modelSimOutput` | Path to the CSV file exporting the model similarity matrix * | Yes | `""` (no export) |
| `assignmentOutput` | Path to the CSV file exporting the assignment data * | Yes | `""` (no export) |
- \* These paths are relative to the `outputDir` directory.

## Output

The Topic Model modules output multiple files.

First, the document JSON file, which follows a similar structure to the lemmas and corpus files:
```json5
{
  "metadata":{...
    "nTopicsMain":20,
    "nTopicsSub":30
  },
  "documents":[
    {
      "docId":"0",
      "docIndex":0,
      "numLemmas":107,
      "docData":{"key": "value", ...},
      "mainTopicDistribution":[ ... ],
      "subTopicDistribution":[ ... ],
      "mainTopicFullWordDistances":[ ... ],
      "subTopicFullWordDistances":[ ... ],
      "mainTopicCompWordDistances":[ ... ],
      "subTopicCompWordDistances":[ ... ]
    },{
      "docId": "1",
      "docIndex": 1,
      "tooShort": true,
      "numLemmas": 2,
      "docData": {"key": "value2", ...}
    },...
  ]
}
```

In addition to the information from the lemmas file, the `metadata` now also contains:
- the number of topics `nTopicsMain`, if the simple Topic Modelling module was used;
- the number of main topics `nTopicsMain` and sub topics `nTopicsSub`, if the Hierarchical Topic Modelling module
was used.

Then the file has a `documents` list, with one object per document with the following information:
- `docId` the document id;
- `docIndex` the document index;
- `numLemmas` the number of lemmas in that document;
- `docData` the document data that was kept with `docFields`;
- if the document inherited the `removed` and `removeReason` attributes from the lemma file, those are kept;
- otherwise, the document has been used in the topic model(s) and now has topic weights data:
    - `mainTopicDistribution` the list of (main) topic weights (regardless of which module was uses);
    - `subTopicDistribution` the list of sub topic weights (if the Hierarchical Topic Modelling module was used);
    - if `wordDistances` was set to true ( This is where the Hellinger Scores are saved):
      - `mainTopicFullWordDistances` the list of word distances between (main) topics and the full document;
      - `mainTopicCompWordDistances` the list of word distances between (main) topics and their related document's components;
      - `subTopicFullWordDistances` the list of word distances between sub topics and the full document (if the Hierarchical Topic Modelling module was used);
      - `subTopicCompWordDistances` the list of word distances between sub topics and their related document's components (if the Hierarchical Topic Modelling module was used).
    
Second, the topic JSON file(s), either one if using the simple Topic Modelling module, or two if using the 
Hierarchical Topic Modelling module. They roughly follow the same structure:
```json5
{
  "metadata": {...
    "nTopics": 20,
    "nDocs": 20,
    "nWords": 10
  },
  "topics": [
    {
      "topicId": "0",
      "topicIndex": 0,
      "topDocs": [{"docId": "id", "weight": 0.7778}, ... ],
      "topWords": [{"label": "risk", "weight": 85.0}, ... ],
      "subTopicIds": [ ... ],
      "mainTopicIds": [ ... ]
    }, ...
  ],
  "similarities": [ [ ... ], ... ]
}
```

In addition to the metadata from the lemmas JSON file, the following fields are added:
- the number of topics `nTopics`;
- the number of top documents per topic `nDocs`;
- the number of top words per topic `nWords`.

Then, the file has list of `topics`, with one object per topic with the following fields:
- `topicId` the topic id;
- `topicIndex` the topic index, used for example in the documents' topic distributions or in the `topicSimilarity`;
- `topDocs` the top documents for that topic, with their `docId` and `weight`;
- `topWords` the top words for that topic, with their `label` and `weight`;
- if the Hierarchical Topic Modelling module was used, an additional field is added:
    - `subTopicIds`, if this is the main topic JSON file, containing the list of sub topic ids assigned to that main
    topic;
    - `mainTopicIds`, if this the sub topic JSON file, containing the list of main topic ids assigend to that sub
    topic.
    
Finally, the topic JSON file has `similarities` which contains the similarity matrix between the topics in that file.
The matrix is in the form of a list of list of numbers:
```json5
{...
  "similarities": [
    [0-0, 0-1, 0-2, ..., 0-n],
    [1-0, 1-1, 1-2, ..., 1-n],
    [2-0, 2-1, 2-2, ..., 2-n],
    ...,
    [n-0, n-1, n-2, ..., n-n]
  ]
}
```

---

[< Previous](LemmatiseModule.md) | [Index](index.md) | [Next >](InferenceModule.md)


This work is licensed under a [Creative Commons Attribution 4.0 International
License][cc-by-nc].

[![CC BY-NC 4.0][cc-by-nc-image]][cc-by-nc]

[cc-by-nc]: http://creativecommons.org/licenses/by-nc/4.0/
[cc-by-nc-image]: https://i.creativecommons.org/l/by-nc/4.0/88x31.png
[cc-by-nc-shield]: https://img.shields.io/badge/License-CC%20BY--NC%204.0-lightgrey.svg

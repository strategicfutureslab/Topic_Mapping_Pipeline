# Topic Mapping Pipeline [![CC BY-NC 4.0][cc-by-nc-shield]][cc-by-nc]

[< Previous](TopicClusteringModule.md) | [Index](index.md) | [Next >](OverwriteMapModule.md)

---

# Topic Mapping Modules

The Topic Mapping modules use the data generated by the [Topic Distribution](TopicDistributionModule.md) and 
[Topic Clustering](TopicClusteringModule.md) modules to create mappable data for the topics: position and size of 
topics and clusters' border. 
With main topics, it creates one main map. With sub topics, it will create a set of maps, one per sub topic groups 
(as generated in the [Topic Clustering module](TopicClusteringModule.md)).

The mapping information are saved in ***Map JSON file(s)***.

The Topic Mapping modules are contained in the `P5_TopicMapping` package.

## List of Topic Mapping Modules

There is currently one Topic Mapping module:
- ***BubbleMap*** (`BubbleMapping` package, `BubbleMap.java` class), which generate bubble maps data. 

## Specifications

The Topic Mapping module entry in the project file should have the following structure:
```json5
{...
  "mapTopics": {
    "topics" | "mainTopics": "path",
    "subTopics": "path",
    "output" | "mainOutput": "path",
    "subOutput": "path",
    "mapType": "bubble",
    ...
  },
...}
```

| Name | Description | Optional | Default |
| --- | --- | --- | --- |
| `topics` or `mainTopics` (if the model is hierarchical) | Path to the (main) topics JSON file, with cluster data * | No | |
| `subTopics` | Path to the sub topics JSON file, with cluster data * | Required if the model is hierarchical | `""` ** | 
| `output` or `mainOutput` (if the model is hierarchical) | Path to the output (main) map JSON file *** | No | |
| `subOutput` | Path to the output sub map JSON file *** | Required if the model is hierarchical | |
| `mapType` | Type of map to generate, see below | Yes | `"bubble"` |
- \* These paths are relative to the [data directory](MetaParameters.md);
- \** This default value implies a non-hierarchical model, if the [model type meta-parameter](MetaParameters.md) is set to `hierarchical`, a path must be provided;
- \*** These paths are relative to the [output directory](MetaParameters.md).

There is currently only one `mapType` supported:
- `bubble`: generating a bubble map.

### Bubble Map Specification

If you are setting the `mapType` to `bubble`, here are the additional specifications available:
```json5
{...
  "mapTopics": {
    ...
    "bubbleSize": "-",
    "bubbleScale": [5, 40],
    "targetSize": [1000,1000],
    "padding": 1,
    "curvature": 8
  },
...}
```

| Name | Description | Optional | Default |
| --- | --- | --- | --- |
| `bubbleSize` | Name of the topic distribution to use to set the topic bubbles' sizes, see below | Yes | `"-"` (sum of topic weights across documents) |
| `bubbleScale` | Scale on which to project the bubble sizes, i.e., minimum and maximum bubble radii | Yes | `[5, 40]` |
| `targetSize` | Target width and height for the map | Yes | `[1000, 1000]` |
| `padding` | Minimum distance around a bubble (with other bubbles and borders | Yes | `1` |
| `curvature` | Curvature of tangent/convex border | Yes | `8` |

The Bubble Map assumes that the provided topics have had their distribution generated with the 
[Topic Distribution module](TopicDistributionModule.md). To set bubble sizes, the mapping module will lookup the total 
weight associated with the distribution identified by `bubbleSize`. Distributions are named with string with a 
`fieldName-valueField` structure, as defined in the [distribution's specifications](TopicDistributionModule.md).

## Output

### Bubble Map Output

Used on the main topics, the Topic Mapping module generates a main map JSON file with the following structure:
```json5
{
  "topics": [{
    "topicId": "0",
    "clusterId": "1",
    "size": 150,
    "labels": [{
      "weight": 165,
      "label": "laser"
    }, ... ],
    "bubbleMap": {
      "r": 26.0,
      "cx": 571.0,
      "cy": 526.0
    }
  }, ... ],
  "bubbleMapBorder": [{
    "d": "M7.1,-27.1A28.0,28.0,0,1,1,-26.6,-8.8A28.0,28.0,0,1,0,7.1,-27.1Z",
    "transform": "translate(571.2,526.9)"
  }, ... ]
}
```
`topics` contains the list of all topics to map:
- `topicId` identifies the topic;
- `clusterId` identifies the cluster in which the topic belongs;
- `size` represent the importance of the topic, as computed by the [Topic Distribution module](TopicDistributionModule.md);
- `labels` is the list of labels for the topic, each having a `label` and `weight`;
- `bubbleMap` contains the mapping information for the topic, `cx` and `cy` being the x and y coordinate of the 
bubble center, and `r` the buble radius.

`bubbleMapBorder` contains the mapping detail for the borders to draw around bubbles of the same cluster:
- `d` is the border path;
- `transform` contains the transformation to apply to the border to position it around the right bubbles.

Used on the sub topics, the Topic Mapping module produces a set of map data into one JSON file:
```json5
[{
  "subMap": {
    "topics": [ ... ],
    "bubbleMapBorder": [ ... ]
  },
  "mainTopicId": "0"
}, ... ]
```
With:
- `subMap` containing the map data of the sub topics;
- `mainTopicId` referencing the main topic to which the sub map is assigned to. 

---

[< Previous](TopicClusteringModule.md) | [Index](index.md) | [Next >](OverwriteMapModule.md)

This work is licensed under a [Creative Commons Attribution 4.0 International License][cc-by-nc].

[![CC BY-NC 4.0][cc-by-nc-image]][cc-by-nc]

[cc-by-nc]: http://creativecommons.org/licenses/by-nc/4.0/
[cc-by-nc-image]: https://i.creativecommons.org/l/by-nc/4.0/88x31.png
[cc-by-nc-shield]: https://img.shields.io/badge/License-CC%20BY--NC%204.0-lightgrey.svg

const fs = require('fs');
const d3 = require('../libs/d3.min.js');
//const planck = require('../libs/planck.min.js');
const bubbletreemap = require('../libs/bubbletreemap.js');
const hierarchyData = require('../hierarchy/hierarchyData.js');
const {round, transform, isObject} = require('../libs/lodash.min.js');

(function Main(){

    function LOG(msg, depth){
        let tab = "  ".repeat(depth);
        console.log(tab+" - "+msg)
    }

    // parameters
    let topicFile, mapFile, sizeId, sizeScale, isMain, targetSize, outputFormat;
    // returns
    let topicsData;

    function processArgs(){
        LOG("processing arguments", 1)
        let args = process.argv.slice(2);
        topicFile = args[0];
        mapFile = args[1];
        isMain = (args[2] === "true")
        sizeId = args[3] || "";
        sizeScale = JSON.parse(args[4]) || [5,40];
        targetSize = JSON.parse(args[5]) || [1000,1000];
        outputFormat = args[6] || "full";
        LOG("mapping topics from "+topicFile+" to "+mapFile, 2);
    }

    function readTopics(){
        LOG("reading topics", 1);
        topicsData = JSON.parse(fs.readFileSync(topicFile));
    }

    function buildMap(topics, depth=2){
        if(topics.topics.length == 0){
            LOG("Empty topic group, returning empty map", depth)
            return {
               "topics": [],
               "bubbleMapBorder": []
           }
        }
        LOG("generating hierarchy", depth);
        let hData = hierarchyData.make(topics, sizeId);
        LOG("generating bubble map", depth);
        let bubbleSizeScale = d3.scaleLinear()
            .domain([1, d3.max(hierarchyData.getSizes(hData))])
            .range(sizeScale);
        let root = d3.hierarchy(hData)
            .sum(d => bubbleSizeScale(d.size))
            .sort((a, b) => {return b.value - a.value});

        let bubbleTreeMap = bubbletreemap()
            .padding(1)
            .curvature(8)
            .hierarchyRoot(root)
            .width(targetSize[0])
            .height(targetSize[1]);

        let bubbleMapData = bubbleTreeMap
            .doLayout()
            .hierarchyRoot();

        let bubblesData = bubbleMapData.descendants().filter(c=>{
            return !c.children;
        });
        let bordersData = bubbleTreeMap.getContour().filter(a =>{
            return a.strokeWidth > 0;
        });
        LOG("building map data", depth);
        let tmpTopics = bubblesData.map(d=>{
            let tData = {
                "topicId": d.data.topicData.topicId,
                "clusterId": d.data.topicData.clusterId,
                "size": round(d.data.size, 3),
                "labels": d.data.topicData.topWords,
                "bubbleMap": {
                    "r": round(d.r, 3),
                    "cx": round(d.x, 3),
                    "cy": round(d.y, 3)
                }
            }
            if(typeof d.data.topicData.mainTopicIds !== 'undefined'){
                tData["mainTopicIds"] = d.data.topicData.mainTopicIds
            } else if(typeof d.data.topicData.subTopicIds !== 'undefined'){
                tData["subTopicIds"] = d.data.topicData.subTopicIds
            }
            return tData
        });

        let mapData = {
            "topics": tmpTopics,
            "bubbleMapBorder": bordersData
        }

        return mapData;
    }

    function getKeysMap(){
        if(outputFormat === "short"){
            return {
                "topics": "t",
                "topicId": "tId",
                "clusterId": "cId",
                "size": "s",
                "labels": "l",
                "label": "l",
                "weight": "w",
                "bubbleMap": "bM",
                "mainTopicIds": "mTIds",
                "subTopicIds": "sTIds",
                "bubbleMapBorder": "bMB",
                "transform": "t",
                "strokeWidth": "s",
                "subMap": "sMap",
                "mainTopicId": "mTId"
            }
        }
        return { }
    }

    function replaceKeysDeep(obj, keysMap){
        return transform(obj, function(result, value, key){
            let currentKey = keysMap[key] || key;
            result[currentKey] = isObject(value) ? replaceKeysDeep(value, keysMap) : value;
        })
    }

    function saveMap(data){
        try{
            LOG("formatting data output", 2)
            data = replaceKeysDeep(data, getKeysMap())
            LOG("saving map data", 2);
            fs.writeFileSync(mapFile, JSON.stringify(data));
        } catch(err){
            console.error(err);
        }
    }

    function buildMainMap(){
        LOG("building main map", 1);
        let mapData = buildMap(topicsData);
        saveMap(mapData);
    }

    function buildSubMap(){
        LOG("building sub maps", 1);
        let mapsArray = [];
        for(let group of topicsData.subTopicGroups){
            LOG("building sub map "+group.mainTopicId, 2)
            let mapData = buildMap(group, 3);
            mapsArray.push({
                "subMap": mapData,
                "mainTopicId": group.mainTopicId
            })
        }
        saveMap(mapsArray);
    }

    function start(){
        LOG("Node JS - starting", 0);
        processArgs();
        readTopics();
        if(isMain){
            buildMainMap();
        } else {
            buildSubMap();
        }
        LOG("Node JS - finished", 0);
    }

    start();
})()
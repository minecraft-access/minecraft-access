package org.mcaccess.minecraftaccess.features.point_of_interest;

public class POIManager {
    public LockingHandler lockingHandler;
    public ObjectTracker objectTracker;
    public POIBlocks poiBlocks;
    public POIEntities poiEntities;
    public POIMarking poiMarking;

    public POIManager() {
        lockingHandler = new LockingHandler();
        objectTracker = new ObjectTracker();
        poiBlocks = new POIBlocks();
        poiEntities = new POIEntities();
        poiMarking = new POIMarking();
    }
}

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

    public void tick() {
        poiMarking.tick();
        poiBlocks.tick(poiMarking.isMarked(), poiMarking.getMarkedBlock());
        poiEntities.tick(poiMarking.isMarked(), poiMarking.getMarkedEntity());
        // Locking Handler (POI Locking) should be after POI Scan features
        lockingHandler.tick();
        objectTracker.tick();
    }
}

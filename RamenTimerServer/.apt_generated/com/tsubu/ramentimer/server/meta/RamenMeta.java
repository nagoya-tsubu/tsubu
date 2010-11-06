package com.tsubu.ramentimer.server.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2010-11-05 01:50:16")
/** */
public final class RamenMeta extends org.slim3.datastore.ModelMeta<com.tsubu.ramentimer.server.model.Ramen> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.tsubu.ramentimer.server.model.Ramen, java.lang.Integer> boilTime = new org.slim3.datastore.CoreAttributeMeta<com.tsubu.ramentimer.server.model.Ramen, java.lang.Integer>(this, "boilTime", "boilTime", int.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.tsubu.ramentimer.server.model.Ramen, java.util.Date> createdAt = new org.slim3.datastore.CoreAttributeMeta<com.tsubu.ramentimer.server.model.Ramen, java.util.Date>(this, "createdAt", "createdAt", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreUnindexedAttributeMeta<com.tsubu.ramentimer.server.model.Ramen, byte[]> imageData = new org.slim3.datastore.CoreUnindexedAttributeMeta<com.tsubu.ramentimer.server.model.Ramen, byte[]>(this, "imageData", "imageData", byte[].class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.tsubu.ramentimer.server.model.Ramen> jan = new org.slim3.datastore.StringAttributeMeta<com.tsubu.ramentimer.server.model.Ramen>(this, "jan", "jan");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.tsubu.ramentimer.server.model.Ramen, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<com.tsubu.ramentimer.server.model.Ramen, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.tsubu.ramentimer.server.model.Ramen> name = new org.slim3.datastore.StringAttributeMeta<com.tsubu.ramentimer.server.model.Ramen>(this, "name", "name");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.tsubu.ramentimer.server.model.Ramen, java.util.Date> updatedAt = new org.slim3.datastore.CoreAttributeMeta<com.tsubu.ramentimer.server.model.Ramen, java.util.Date>(this, "updatedAt", "updatedAt", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.tsubu.ramentimer.server.model.Ramen, java.lang.Long> version = new org.slim3.datastore.CoreAttributeMeta<com.tsubu.ramentimer.server.model.Ramen, java.lang.Long>(this, "version", "version", java.lang.Long.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAtAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_updatedAtAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final RamenMeta slim3_singleton = new RamenMeta();

    /**
     * @return the singleton
     */
    public static RamenMeta get() {
       return slim3_singleton;
    }

    /** */
    public RamenMeta() {
        super("Ramen", com.tsubu.ramentimer.server.model.Ramen.class);
    }

    @Override
    public com.tsubu.ramentimer.server.model.Ramen entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.tsubu.ramentimer.server.model.Ramen model = new com.tsubu.ramentimer.server.model.Ramen();
        model.setBoilTime(longToPrimitiveInt((java.lang.Long) entity.getProperty("boilTime")));
        model.setCreatedAt((java.util.Date) entity.getProperty("createdAt"));
        model.setImageData(blobToBytes((com.google.appengine.api.datastore.Blob) entity.getProperty("imageData")));
        model.setJan((java.lang.String) entity.getProperty("jan"));
        model.setKey(entity.getKey());
        model.setName((java.lang.String) entity.getProperty("name"));
        model.setUpdatedAt((java.util.Date) entity.getProperty("updatedAt"));
        model.setVersion((java.lang.Long) entity.getProperty("version"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.tsubu.ramentimer.server.model.Ramen m = (com.tsubu.ramentimer.server.model.Ramen) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("boilTime", m.getBoilTime());
        entity.setProperty("createdAt", m.getCreatedAt());
        entity.setUnindexedProperty("imageData", bytesToBlob(m.getImageData()));
        entity.setProperty("jan", m.getJan());
        entity.setProperty("name", m.getName());
        entity.setProperty("updatedAt", m.getUpdatedAt());
        entity.setProperty("version", m.getVersion());
        entity.setProperty("slim3.schemaVersion", 1);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.tsubu.ramentimer.server.model.Ramen m = (com.tsubu.ramentimer.server.model.Ramen) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.tsubu.ramentimer.server.model.Ramen m = (com.tsubu.ramentimer.server.model.Ramen) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        com.tsubu.ramentimer.server.model.Ramen m = (com.tsubu.ramentimer.server.model.Ramen) model;
        return m.getVersion() != null ? m.getVersion().longValue() : 0L;
    }

    @Override
    protected void incrementVersion(Object model) {
        com.tsubu.ramentimer.server.model.Ramen m = (com.tsubu.ramentimer.server.model.Ramen) model;
        long version = m.getVersion() != null ? m.getVersion().longValue() : 0L;
        m.setVersion(Long.valueOf(version + 1L));
    }

    @Override
    protected void prePut(Object model) {
        assignKeyIfNecessary(model);
        incrementVersion(model);
        com.tsubu.ramentimer.server.model.Ramen m = (com.tsubu.ramentimer.server.model.Ramen) model;
        m.setCreatedAt(slim3_createdAtAttributeListener.prePut(m.getCreatedAt()));
        m.setUpdatedAt(slim3_updatedAtAttributeListener.prePut(m.getUpdatedAt()));
    }

    @Override
    public String getSchemaVersionName() {
        return "slim3.schemaVersion";
    }

    @Override
    public String getClassHierarchyListName() {
        return "slim3.classHierarchyList";
    }

}
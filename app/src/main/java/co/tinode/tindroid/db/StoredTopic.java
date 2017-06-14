package co.tinode.tindroid.db;

import android.database.Cursor;
import android.util.Log;

import java.util.Date;

import co.tinode.tinodesdk.LocalData;
import co.tinode.tinodesdk.Topic;

/**
 * Representation of a topic stored in a database;
 */
public class StoredTopic<Pu,Pr,T> implements LocalData.Payload {
    private static final String TAG = "StoredTopic";

    public long id;
    public Date lastUsed;
    public int minLocalSeq;
    public int maxLocalSeq;
    public int status;

    public StoredTopic() {
    }

    @SuppressWarnings("unchecked")
    protected static <Pu,Pr,T> void deserialize(Topic<Pu,Pr,T> topic, Cursor c) {
        StoredTopic<Pu,Pr,T> st = new StoredTopic<>();

        st.id = c.getLong(TopicDb.COLUMN_IDX_ID);
        st.status = c.getInt(TopicDb.COLUMN_IDX_STATUS);
        st.lastUsed = new Date(c.getLong(TopicDb.COLUMN_IDX_LASTUSED));
        st.minLocalSeq = c.getInt(TopicDb.COLUMN_IDX_MIN_LOCAL_SEQ);
        st.maxLocalSeq = c.getInt(TopicDb.COLUMN_IDX_MAX_LOCAL_SEQ);

        topic.setUpdated(new Date(c.getLong(TopicDb.COLUMN_IDX_UPDATED)));

        topic.setRead(c.getInt(TopicDb.COLUMN_IDX_READ));
        topic.setRecv(c.getInt(TopicDb.COLUMN_IDX_RECV));
        topic.setSeq(c.getInt(TopicDb.COLUMN_IDX_SEQ));
        topic.setClear(c.getInt(TopicDb.COLUMN_IDX_CLEAR));

        topic.setSerializedTypes(c.getString(TopicDb.COLUMN_IDX_SERIALIZED_TYPES));
        topic.setPub((Pu) BaseDb.deserialize(c.getBlob(TopicDb.COLUMN_IDX_PUBLIC)));
        topic.setPriv((Pr) BaseDb.deserialize(c.getBlob(TopicDb.COLUMN_IDX_PRIVATE)));

        topic.setAccessMode(BaseDb.deserializeMode(c.getString(TopicDb.COLUMN_IDX_ACCESSMODE)));
        topic.setDefacs(BaseDb.deserializeDefacs(c.getString(TopicDb.COLUMN_IDX_DEFACS)));

        topic.setLocal(st);
    }

    public static long getId(Topic topic) {
        StoredTopic st = (StoredTopic) topic.getLocal();
        return st != null ? st.id : -1;
    }

    public static boolean isAllDataLoaded(Topic topic) {
        StoredTopic st = (StoredTopic) topic.getLocal();
        Log.d(TAG, "Is all data loaded? " + (st == null ? "st=null" : "st.min=" + st.minLocalSeq) + ", topic.seq=" + topic.getSeq());
        return topic.getSeq() == 0 || (st != null && st.minLocalSeq == 1);
    }
}

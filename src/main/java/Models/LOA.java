package Models;

import com.mewna.catnip.entity.builder.EmbedBuilder;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public class LOA {
    private ObjectId id;
    private long userId;
    private Date startDate;
    private Date endDate;
    private String reason;
    private List<Long> needApprovedBy;
    private List<Long> approvedBy;
    private String color;

    public LOA(long userId, Date startDate, Date endDate, String reason) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }

    public LOA(){}

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<Long> getNeedApprovedBy() {
        return needApprovedBy;
    }

    public void setNeedApprovedBy(List<Long> needApprovedBy) {
        this.needApprovedBy = needApprovedBy;
    }

    public List<Long> getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(List<Long> approvedBy) {
        this.approvedBy = approvedBy;
    }

    public void addAprrovedBy(long id){
        this.approvedBy.add(id);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

package teq.development.seatech.CustomCalendar;

public class EventSkeleton {

    String eventJobID;
    String eventPdfUrl;

    public String getEventJobID() {
        return eventJobID;
    }

    public void setEventJobID(String eventJobID) {
        this.eventJobID = eventJobID;
    }

    public String getEventPdfUrl() {
        return eventPdfUrl;
    }

    public void setEventPdfUrl(String eventPdfUrl) {
        this.eventPdfUrl = eventPdfUrl;
    }

    public String getEventCustomer() {
        return eventCustomer;
    }

    public void setEventCustomer(String eventCustomer) {
        this.eventCustomer = eventCustomer;
    }

    String eventCustomer;
}

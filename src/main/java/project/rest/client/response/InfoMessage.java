package project.rest.client.response;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InfoMessage {

    private int status;
    private String message;

    public InfoMessage() {
    }

    public InfoMessage(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

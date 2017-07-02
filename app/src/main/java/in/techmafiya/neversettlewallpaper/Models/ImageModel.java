package in.techmafiya.neversettlewallpaper.Models;

/**
 * Created by saikiran on 6/12/17.
 */

public class ImageModel {

    public String uid;
    public String f;
    public String s;
    public String ws;
    public String wf;
    public int likes;
    public String author;

    public ImageModel(String uid, String f, String s, String ws, String wf, int likes, String author) {
        this.uid = uid;
        this.f = f;
        this.s = s;
        this.ws = ws;
        this.wf = wf;
        this.likes = likes;
        this.author = author;
    }

    public ImageModel() { // defalt const.
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getWs() {
        return ws;
    }

    public void setWs(String ws) {
        this.ws = ws;
    }

    public String getWf() {
        return wf;
    }

    public void setWf(String wf) {
        this.wf = wf;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageModel)) return false;

        ImageModel that = (ImageModel) o;

        return getUid() != null ? getUid().equals(that.getUid()) : that.getUid() == null;

    }

    @Override
    public int hashCode() {
        return getUid() != null ? getUid().hashCode() : 0;
    }
}

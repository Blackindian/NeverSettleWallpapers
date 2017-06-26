package in.techmafiya.neversettlewallpaper.Models;

/**
 * Created by saikiran on 6/12/17.
 */

public class ImageModel {

    public String uid;
    public String f;
    public String s;

    public ImageModel(String uid, String f, String s) {
        this.uid = uid;
        this.f = f;
        this.s = s;
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

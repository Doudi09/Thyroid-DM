package Data;

import java.util.Objects;

public class Thyroid {


    private int classe;
    private float t3; //Test d’absorption de la résine T3
    private float tys; //Thyroxine sérique totale
    private float tri; //Triiodothyronine sérique totale
    private float tsh; //Hormone basale de stimulation de la thyroïde
    private float dtsh; //Différence absolue maximale de la valeur TSH après injection de 200 micro
    // grammes d’hormone de libération de la thyrotropine par rapport à la valeur basale.

    public Thyroid() {

    }


    public Thyroid(int classe, float t3, float tys, float tri, float tsh, float dtsh) {
        this.classe = classe;
        this.t3 = t3;
        this.tys = tys;
        this.tri = tri;
        this.tsh = tsh;
        this.dtsh = dtsh;
    }

    public int getClasse() {
        return classe;
    }

    public void setClasse(int classe) {
        this.classe = classe;
    }

    public float getT3() {
        return t3;
    }

    public void setT3(float t3) {
        this.t3 = t3;
    }

    public float getTys() {
        return tys;
    }

    public void setTys(float tys) {
        this.tys = tys;
    }

    public float getTri() {
        return tri;
    }

    public void setTri(float tri) {
        this.tri = tri;
    }

    public float getTsh() {
        return tsh;
    }

    public void setTsh(float tsh) {
        this.tsh = tsh;
    }

    public float getDtsh() {
        return dtsh;
    }

    public void setDtsh(float dtsh) {
        this.dtsh = dtsh;
    }

    public Float getAttributeValue(String attributeName) {
        switch (attributeName) {
            case "t3":
                return t3;
            case "tri":
                return tri;
            case "tsh":
                return tsh;
            case "tys":
                return tys;
            case "dtsh":
                return dtsh;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return "Thyroid{" +
                "classe = " + classe +
                ", t3 = " + t3 +
                ", tys = " + tys +
                ", tri = " + tri +
                ", tsh = " + tsh +
                ", dtsh  =" + dtsh +
                '}';
    }


    public double euclideinDistance(Thyroid t1) {
        double sum = 0;
        sum = Math.pow(t1.dtsh - dtsh, 2) +
                Math.pow(t1.t3 - t3, 2) +
                Math.pow(t1.tri - tri, 2) +
                Math.pow(t1.tsh - tsh, 2) +
                Math.pow(t1.tys - tys, 2);

        return Math.sqrt(sum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Thyroid)) return false;
        Thyroid thyroid = (Thyroid) o;
        return classe == thyroid.classe &&
                thyroid.t3 == t3 &&
                thyroid.tys == tys &&
                thyroid.tri == tri &&
                thyroid.tsh == tsh &&
                thyroid.dtsh == dtsh;
    }

    @Override
    public int hashCode() {
        return Objects.hash(classe, t3, tys, tri, tsh, dtsh);
    }
}

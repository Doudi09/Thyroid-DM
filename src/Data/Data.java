package Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.List;

public class Data {

    public static String T3 = "t3";
    public static String TRI = "tri";
    public static String TSH = "tsh";
    public static String TYS = "tys";
    public static String DTSH = "dtsh";

    public static int c1 = 150;
    public static int c2 = 35;
    public static int c3 = 30;

    public static String PATH = "D:\\M2 SII 2021\\2021\\DM\\Projet\\Projet datamining 2020-2021\\Thyroid_Dataset.txt";
    private ArrayList<Thyroid> dataset;
    private int lenght;


    public Data(String path) {

        dataset = readFile(path);
        lenght = dataset.size();
    }

    public Data() {
        dataset = readFile(PATH);
        lenght = dataset.size();
    }
    public Data(boolean preprocess){
        //forte corrélation entre TYS et TRI donc je garde que l'att TYS
        // bq de outliers dans les att TRI TSH DTSH
        // donc les att restants : T3, TYS
        dataset = readFile(PATH);
        ArrayList<Thyroid> liste = new ArrayList();
        double barreSupT3 = 140.5;
        double barreInfT3 = 80.5;

        double barreSupTys = 0.79;
        double barreInfTys = 17.6;

        for (Thyroid t : this.dataset){
            if ( ! ((t.getT3() < barreInfT3 || t.getT3()>barreSupT3) &&
                    (t.getTys() < barreInfTys || t.getTys()>barreSupTys))){
            liste.add(t);
            }
        }
        this.dataset = liste;
        this.lenght = this.dataset.size();

    }

    public float mean(String attributeName) {
        float mean = 0;
        for (Thyroid t : dataset) {
            mean = mean + t.getAttributeValue(attributeName);
        }
        mean = mean / lenght;
        return mean;
    }

    public float median(String attributeName) {
        //sorting the data :
        Collections.sort(dataset, new Comparator<Thyroid>() {
            @Override
            public int compare(Thyroid o1, Thyroid o2) {
                if(o1.getAttributeValue(attributeName) > o2.getAttributeValue(attributeName))
                    return 1;
                if(o1.getAttributeValue(attributeName) < o2.getAttributeValue(attributeName))
                    return -1;
                return 0;
            }
        });

        if (lenght % 2 == 0) {
            //the lenght is even so we take the avg between values in lenght/2 and lenght/2-1
            float med = dataset.get((int) (lenght / 2)).getAttributeValue(attributeName) +
                    dataset.get((int) (lenght / 2) - 1).getAttributeValue(attributeName);
            return med / 2;
        } else
            //the lenght is odd so we take the value is pos : lenght/2 (without adding 1 cuz we start from 0)
            return dataset.get((int) (lenght / 2)).getAttributeValue(attributeName);
    }

    public ArrayList<Float> mode(String attributeName) {
        ArrayList<Float> mode = new ArrayList<>();
        //i create a map <attribute value , freq>
        // then ill take the max freq attribute value as the mode
        // isn case we have more then one ill return an array !
        HashMap<Float, Integer> map = new HashMap<>();
        for (Thyroid t : dataset) {
            if (map.containsKey(t.getAttributeValue(attributeName)))
                map.replace(t.getAttributeValue(attributeName),
                        map.get(t.getAttributeValue(attributeName)) + 1
                );
            else
                map.put(t.getAttributeValue(attributeName), 1);
        }
        //sorting the map by values in sortedMap
        List<Map.Entry<Float, Integer>> sortedMap = new ArrayList<>(map.entrySet());
        Collections.sort(sortedMap, new Comparator<Map.Entry<Float, Integer>>() {
            public int compare(Map.Entry<Float, Integer> o1,
                               Map.Entry<Float, Integer> o2) {

                if(o1.getValue() < o2.getValue())
                    return 1;
                if(o1.getValue() > o2.getValue())
                    return -1;
                return 0;
            }
        });

        int max = sortedMap.get(0).getValue();
        for (Map.Entry<Float, Integer> line : sortedMap) {
            if (max == line.getValue())
                mode.add(line.getKey());
            else break;
        }
        return mode;
    }

    public boolean isSymetric(String attributeName) {

        float mean = mean(attributeName);
        ArrayList<Float> mode = mode(attributeName);
        float modeValue = mode.get(0);
        float median = median(attributeName);

        return (median == mean && mean == modeValue);
    }

    public boolean isSymetric(float mode, float median, float mean) {

        return (median == mean && mean == mode);
    }

    public String modeType(String attributeName) {
        ArrayList<Float> mode = mode(attributeName);
        switch (mode.size()) {
            case 1:
                return "Le mode est unimodal";
            case 2:
                return "Le mode est bimodal";
            default:
                return "Le mode est multimodal";
        }
    }

    public String modeType(ArrayList<Float> mode) {
        switch (mode.size()) {
            case 1:
                return "Le mode est unimodal";
            case 2:
                return "Le mode est bimodal";
            default:
                return "Le mode est multimodal";
        }
    }

    public float max(String attributeName) {
        float max = Collections.max(dataset, new Comparator<Thyroid>() {
            @Override
            public int compare(Thyroid o1, Thyroid o2) {
                if(o1.getAttributeValue(attributeName) > o2.getAttributeValue(attributeName))
                    return 1;
                if(o1.getAttributeValue(attributeName) < o2.getAttributeValue(attributeName))
                    return -1;
                return 0;
            }
        }).getAttributeValue(attributeName);
        return max;
    }

    public float min(String attributeName) {
        float min = Collections.min(dataset, new Comparator<Thyroid>() {
            @Override
            public int compare(Thyroid o1, Thyroid o2) {
                if(o1.getAttributeValue(attributeName) > o2.getAttributeValue(attributeName))
                    return 1;
                if(o1.getAttributeValue(attributeName) < o2.getAttributeValue(attributeName))
                    return -1;
                return 0;
            }
        }).getAttributeValue(attributeName);
        return min;
    }

    public ArrayList<Integer> classDistribution() {
        ArrayList<Integer> dist = new ArrayList<>();
        //we have 3 classes 1, 2 and 3 so :
        //dist[0] contains the freq of class 1
        //dist[1] contains the freq of class 2
        //dist[2] contains the freq of class 3
        dist.add(0);
        dist.add(0);
        dist.add(0);
        for (Thyroid t : dataset) {
            dist.set(t.getClasse() - 1, dist.get(t.getClasse() - 1) + 1);
        }
        return dist;
    }

    public HashMap<String, String> attributeDescription(String attributeName) {
        HashMap<String, String> desc = new HashMap<>();
        desc.put("Nom abrivé  d'attribut : ", attributeName.toUpperCase());
        desc.put("La valeur max : ", "" + max(attributeName));
        desc.put("La valeur min : ", "" + min(attributeName));
        desc.put("Type : ", "attribut numérique continu");

        switch (attributeName) {
            case "t3":
                desc.put("Description d'attribut : ", "Test d’absorption de la résine T3, donnée comme un pourcentage");
                break;
            case "tri":
                desc.put("Description d'attribut : ", "Triiodothyronine sérique totale mesurée par dosage radio-immunologique");
                break;
            case "tsh":
                desc.put("Description d'attribut : ", "Hormone basale de stimulation de la thyroïde telle que mesurée par dosage radio-immunologique");
                break;
            case "tys":
                desc.put("Description d'attribut : ", "Thyroxine sérique totale mesurée par la méthode de déplacement isotopique");
                break;
            case "dtsh":
                desc.put("Description d'attribut : ", "Différence absolue maximale de la valeur TSH après injection de 200 micro grammes d’hormone de libération de la thyrotropine par rapport à la valeur basale");
                break;
            default:
                break;
        }
        return desc;
    }

    public HashMap<String, String> classDescription() {
        HashMap<String, String> desc = new HashMap<>();
        desc.put("Nom d'attribut : ", "Classe");
        desc.put("Type : ", "attribut nominal");
        desc.put("Le nombre de Classes : ", "3");
        desc.put("La description des Classes : ", "1 : Nomrmale(euthyroidie)\n2 : Hyper(hyperthyroidie)\n3 : Hypo (hypothyroidie)");
        return desc;
    }

    public static ArrayList<Thyroid> readFile(String filePath) {
        try {
            ArrayList<Thyroid> dataset = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            List<String> objectLine = new ArrayList<>();
            Thyroid object;
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                objectLine = Arrays.asList(line.split(","));

                int classe = Integer.valueOf(objectLine.get(0));
                float t3 = Float.valueOf(objectLine.get(1));
                float tys = Float.valueOf(objectLine.get(2));
                float tri = Float.valueOf(objectLine.get(3));
                float tsh = Float.valueOf(objectLine.get(4));
                float dtsh = Float.valueOf(objectLine.get(5));

                object = new Thyroid(classe, t3, tys, tri, tsh, dtsh);
                //System.out.println(object.toString());
                dataset.add(object);
            }
            reader.close();
            return dataset;
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", filePath);
            e.printStackTrace();
            return null;
        }
    }

    public void deleteOutliers(){
        ArrayList<Thyroid> liste = new ArrayList();
        double barreSupT3 = 140.5;
        double barreInfT3 = 80.5;

        double barreSupTri = -0.05;
        double barreInfTri = 3.55;

        double barreSupTsh = -0.04;
        double barreInfTsh = 2.75;

        double barreSupTys = 0.79;
        double barreInfTys = 17.6;

        double barreSupDtsh = 9.5;
        double barreInfDtsh = -4.89;

        for (Thyroid t : this.dataset){
            if ( ! ((t.getT3() < barreInfT3 || t.getT3()>barreSupT3) ||
                    (t.getTri() < barreInfTri || t.getTri()>barreSupTri) ||
                    (t.getTsh() < barreInfTsh || t.getTsh()>barreSupTsh) ||
                    (t.getTys() < barreInfTys || t.getTys()>barreSupTys) ||
                    (t.getDtsh() < barreInfDtsh || t.getDtsh()>barreSupDtsh))){
                liste.add(t);
            }
        }
        this.dataset = liste;
        this.lenght = this.dataset.size();
    }


    public ArrayList<Thyroid> getDataset(){
        return dataset;
    }
    public int getLenght(){
        return lenght;
    }


    public ArrayList<Thyroid> getThyroidsClass(int classe){
        ArrayList<Thyroid> listeClasse = new ArrayList<>();
        for (Thyroid t : dataset){
            if (t.getClasse()==classe)
                listeClasse.add(t);
        }
        return listeClasse;
    }

}

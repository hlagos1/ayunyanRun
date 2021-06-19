package lawtechthai.com.ayunyanRun;

public class List_Data {
    private String equipmentID;
    private String certification;

    public List_Data(String pEquipmentID, String pCertification) {
        this.equipmentID = pEquipmentID;
        this.certification = pCertification;

    }

    public String getEquipmentID() {
        return equipmentID;
    }

    public String getCertification() {
        return certification;
    }
}
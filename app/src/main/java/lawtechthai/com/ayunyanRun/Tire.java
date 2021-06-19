package lawtechthai.com.ayunyanRun;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tire {

    private String equipmentID;
    private String operationalStatus;
    private String assetType;
    private String assetCode;
    private String serialNumber;
    private String supplierName;
    private String certificationExpiryDate;
    private String assetOwnerName;
    private String assetOwnerPhoneNumber;
    private String assetOwnerEmail;
    private String assetOwnerLineID;
    private String maker;
    private String model;
    private int mileage;
    private int maximumMileageSpecifications;
    private double treadDepth;
    private double minimumTreadDepthSpecifications;
    private String installedOn;
    private String wheelWell;
    private String dateInService;
    private String dateOfManufacture;
    private String serviceType;
    private String width;
    private String aspectRatio;
    private String construction;
    private String rimDiameter;
    private String loadIndex;
    private String speedRating;
    private String treadRating;
    private String tractionRating;
    private String temperatureRating;
    private String coldTirePressureSpecifications;
    private double latitude;
    private double longitude;
    private String status;
    private String mileageStatus;
    private String treadDepthStatus;
    private String tireAgeStatus;
    private String certificationStatus;
    private long tireAge;

    public Tire() {
    }

    //  Methods

    public void buildTire(JSONObject response) throws JSONException {

        setEquipmentID(response.getString("equipmentID"));
        setOperationalStatus(response.getString("operationalStatus"));
        setAssetType(response.getString("assetType"));
        setAssetCode(response.getString("assetCode"));
        setSerialNumber(response.getString("serialNumber"));
        setCertificationExpiryDate(response.getString("certificationExpiryDate"));
        setAssetOwnerName(response.getString("assetOwnerName"));
        setAssetOwnerPhoneNumber(response.getString("assetOwnerPhoneNumber"));
        setAssetOwnerEmail(response.getString("assetOwnerEmail"));
        setAssetOwnerLineID(response.getString("assetOwnerLineID"));
        setSupplierName(response.getString("supplierName"));
        setMaker(response.getString("maker"));
        setModel(response.getString("model"));
        setMileage(Integer.parseInt(response.getString("mileage")));
        setMaximumMileageSpecifications(Integer.parseInt(response.getString("maximumMileageSpecifications")));
        setTreadDepth(Double.parseDouble(response.getString("treadDepth")));
        setMinimumTreadDepthSpecifications(Double.parseDouble(response.getString("minimumTreadDepthSpecifications")));
        setInstalledOn(response.getString("installedOn"));
        setWheelWell(response.getString("wheelWell"));
        setDateInService(response.getString("dateInService"));
        setDateOfManufacture(response.getString("dateOfManufacture"));
        setServiceType(response.getString("serviceType"));
        setWidth(response.getString("width"));
        setAspectRatio(response.getString("aspectRatio"));
        setConstruction(response.getString("construction"));
        setRimDiameter(response.getString("rimDiameter"));
        setLoadIndex(response.getString("loadIndex"));
        setSpeedRating(response.getString("speedRating"));
        setTreadRating(response.getString("treadRating"));
        setTractionRating(response.getString("tractionRating"));
        setTemperatureRating(response.getString("temperatureRating"));
        setColdTirePressureSpecifications(response.getString("coldTirePressureSpecifications"));
        setLatitude(Double.parseDouble(response.getString("latitude")));
        setLongitude(Double.parseDouble(response.getString("longitude")));
    }

    public void checkStatus() {

        checkTireMileage();
        checkTreadDepth();
        checkCertification();
        checkTireAge();

        if (mileageStatus == "READY" && treadDepthStatus == "READY" && certificationStatus == "READY" && tireAgeStatus == "READY") {
            this.status = "READY";
        } else if (mileageStatus == "WARNING" || treadDepthStatus == "WARNING" || certificationStatus == "WARNING" || tireAgeStatus == "WARNING") {
            this.status = "WARNING";
        } else if (mileageStatus == "NOT READY" || treadDepthStatus == "NOT READY" || certificationStatus == "NOT READY" || tireAgeStatus == "NOT READY") {
            this.status = "NOT READY";
        }
    }

    public void checkTireMileage() {

        if (mileage < maximumMileageSpecifications - 10000) {
            mileageStatus = "READY";
        } else if (mileage < maximumMileageSpecifications && mileage >= maximumMileageSpecifications - 10000) {
            mileageStatus = "WARNING";
        } else if (mileage >= maximumMileageSpecifications) {
            mileageStatus = "NOT READY";
        } else mileageStatus = "UNKNOWN";

    }

    public void checkTreadDepth() {

        if (treadDepth < minimumTreadDepthSpecifications) {
            this.treadDepthStatus = "NOT READY";
        }
        if (treadDepth >= minimumTreadDepthSpecifications && treadDepth <= minimumTreadDepthSpecifications + 1) {
            this.treadDepthStatus = "WARNING";
        } else if (treadDepth > minimumTreadDepthSpecifications + 1) {
            this.treadDepthStatus = "READY";
        } else this.treadDepthStatus = "UNKNOWN";
    }

    public void checkCertification() {
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar todayCalendar = Calendar.getInstance();
        Calendar oneWeekAheadCalendar = Calendar.getInstance();
        oneWeekAheadCalendar.add(Calendar.DAY_OF_YEAR, 7);
        String pToday = sdf.format(todayCalendar.getTime());
        String pOneWeekAhead = sdf.format(oneWeekAheadCalendar.getTime());

        Date dateCertificationExpiryDate = null;
        Date today = null;
        Date oneWeekAhead = null;
        Date tenYearsAgo = null;

        try {
            dateCertificationExpiryDate = sdf.parse(certificationExpiryDate);
            today = sdf.parse(pToday);
            oneWeekAhead = sdf.parse(pOneWeekAhead);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert dateCertificationExpiryDate != null;
        assert today != null;
        assert oneWeekAhead != null;
        assert tenYearsAgo != null;

        if (dateCertificationExpiryDate.after(oneWeekAhead) || dateCertificationExpiryDate.equals(oneWeekAhead)) {
            this.certificationStatus = "READY";
        } else if (dateCertificationExpiryDate.before(oneWeekAhead) && dateCertificationExpiryDate.after(today)) {
            this.certificationStatus = "WARNING";
        } else certificationStatus = "NOT READY";

    }

    public void checkTireAge() {

        tireAge();
        if (tireAge > 10) {
            this.tireAgeStatus = "NOT READY";
        } else if (tireAge <= 10 && tireAge > 9) {
            this.tireAgeStatus = "WARNING";
        } else this.tireAgeStatus = "READY";
    }

    public long tireAge() {

        String pattern = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar todayCalendar = Calendar.getInstance();
        String pToday = sdf.format(todayCalendar.getTime());

        Date dateDateOfManufacture = null;
        Date today = null;

        try {
            dateDateOfManufacture = sdf.parse(dateOfManufacture);
            today = sdf.parse(pToday);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert dateDateOfManufacture != null;
        assert today != null;

        long diff = today.getTime() - dateDateOfManufacture.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = (hours / 24) + 1;
        tireAge = (days / 365);
        return tireAge;
    }

    //  Getters and Setters

    public String getEquipmentID() {
        return equipmentID;
    }

    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    public String getOperationalStatus() {
        return operationalStatus;
    }

    public void setOperationalStatus(String operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getCertificationExpiryDate() {
        return certificationExpiryDate;
    }

    public void setCertificationExpiryDate(String certificationExpiryDate) {
        this.certificationExpiryDate = certificationExpiryDate;
    }

    public String getAssetOwnerName() {
        return assetOwnerName;
    }

    public void setAssetOwnerName(String assetOwnerName) {
        this.assetOwnerName = assetOwnerName;
    }

    public String getAssetOwnerPhoneNumber() {
        return assetOwnerPhoneNumber;
    }

    public void setAssetOwnerPhoneNumber(String assetOwnerPhoneNumber) {
        this.assetOwnerPhoneNumber = assetOwnerPhoneNumber;
    }

    public String getAssetOwnerEmail() {
        return assetOwnerEmail;
    }

    public void setAssetOwnerEmail(String assetOwnerEmail) {
        this.assetOwnerEmail = assetOwnerEmail;
    }

    public String getAssetOwnerLineID() {
        return assetOwnerLineID;
    }

    public void setAssetOwnerLineID(String assetOwnerLineID) {
        this.assetOwnerLineID = assetOwnerLineID;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public int getMaximumMileageSpecifications() {
        return maximumMileageSpecifications;
    }

    public void setMaximumMileageSpecifications(int maximumMileageSpecifications) {
        this.maximumMileageSpecifications = maximumMileageSpecifications;
    }

    public double getTreadDepth() {
        return treadDepth;
    }

    public void setTreadDepth(double treadDepth) {
        this.treadDepth = treadDepth;
    }

    public double getMinimumTreadDepthSpecifications() {
        return minimumTreadDepthSpecifications;
    }

    public void setMinimumTreadDepthSpecifications(double minimumTreadDepthSpecifications) {
        this.minimumTreadDepthSpecifications = minimumTreadDepthSpecifications;
    }

    public String getInstalledOn() {
        return installedOn;
    }

    public void setInstalledOn(String installedOn) {
        this.installedOn = installedOn;
    }

    public String getWheelWell() {
        return wheelWell;
    }

    public void setWheelWell(String wheelWell) {
        this.wheelWell = wheelWell;
    }

    public String getDateInService() {
        return dateInService;
    }

    public void setDateInService(String dateInService) {
        this.dateInService = dateInService;
    }

    public String getDateOfManufacture() {
        return dateOfManufacture;
    }

    public void setDateOfManufacture(String dateOfManufacture) {
        this.dateOfManufacture = dateOfManufacture;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(String aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getConstruction() {
        return construction;
    }

    public void setConstruction(String construction) {
        this.construction = construction;
    }

    public String getRimDiameter() {
        return rimDiameter;
    }

    public void setRimDiameter(String rimDiameter) {
        this.rimDiameter = rimDiameter;
    }

    public String getLoadIndex() {
        return loadIndex;
    }

    public void setLoadIndex(String loadIndex) {
        this.loadIndex = loadIndex;
    }

    public String getSpeedRating() {
        return speedRating;
    }

    public void setSpeedRating(String speedRating) {
        this.speedRating = speedRating;
    }

    public String getTreadRating() {
        return treadRating;
    }

    public void setTreadRating(String treadRating) {
        this.treadRating = treadRating;
    }

    public String getTractionRating() {
        return tractionRating;
    }

    public void setTractionRating(String tractionRating) {
        this.tractionRating = tractionRating;
    }

    public String getTemperatureRating() {
        return temperatureRating;
    }

    public void setTemperatureRating(String temperatureRating) {
        this.temperatureRating = temperatureRating;
    }

    public String getColdTirePressureSpecifications() {
        return coldTirePressureSpecifications;
    }

    public void setColdTirePressureSpecifications(String coldTirePressureSpecifications) {
        this.coldTirePressureSpecifications = coldTirePressureSpecifications;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public String getMileageStatus() {
        return mileageStatus;
    }

    public String getTreadDepthStatus() {
        return treadDepthStatus;
    }

    public String getTireAgeStatus() {
        return tireAgeStatus;
    }

    public String getCertificationStatus() {
        return certificationStatus;
    }

    public long getTireAge() {
        return tireAge;
    }
}

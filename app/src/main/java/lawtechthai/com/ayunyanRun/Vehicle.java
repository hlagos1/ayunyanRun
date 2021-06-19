package lawtechthai.com.ayunyanRun;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Vehicle {

    private String equipmentID;
    private String operationalStatus;
    private String assetType;
    private String assetCode;
    private String serialNumber;
    private String provinceOfRegistration;
    private String plateNumber;
    private String assetOwnerName;
    private String assetOwnerPhoneNumber;
    private String assetOwnerEmail;
    private String assetOwnerLineID;
    private String driver;
    private String driverTrainingExpiryDate;
    private String phone;
    private String certificationExpiryDate;
    private String certificationPerformedBy;
    private String maintenanceExpiryDate;
    private String maintenancePerformedBy;
    private String registrationExpiryDate;
    private String registrationPerformedBy;
    private String insuranceExpiryDate;
    private String insurancePerformedBy;
    private String company;
    private String maker;
    private int odometer;
    private String vin;
    private String engineNumber;
    private String belt;
    private String hose;
    private String oilLeak;
    private String aircon;
    private String wiper;
    private String headHi;
    private String headLo;
    private String drivingLights;
    private String turnSignal;
    private String brakeLights;
    private String hazardLights;
    private String doorLocks;
    private String windshield;
    private String windows;
    private String radio;
    private String horn;
    private String fireExtinguisher;
    private String firstAid;
    private String coolant;
    private String engineOil;
    private String transmissionOil;
    private String powerSteeringFluid;
    private String brakeFluid;
    private String washerFluid;
    private String beltRemarks;
    private String hoseRemarks;
    private String oilLeakRemarks;
    private String airconRemarks;
    private String wiperRemarks;
    private String headHiRemarks;
    private String headLoRemarks;
    private String drivingLightsRemarks;
    private String turnSignalRemarks;
    private String brakeLightsRemarks;
    private String hazardLightsRemarks;
    private String doorLocksRemarks;
    private String windshieldRemarks;
    private String windowsRemarks;
    private String radioRemarks;
    private String hornRemarks;
    private String fireExtinguisherRemarks;
    private String firstAidRemarks;
    private String coolantSpecs;
    private String coolantVolume;
    private String coolantRemarks;
    private String engineOilSpecs;
    private String engineOilVolume;
    private String engineOilRemarks;
    private String transmissionOilSpecs;
    private String transmissionOilVolume;
    private String transmissionOilRemarks;
    private String powerSteeringFluidSpecs;
    private String powerSteeringFluidVolume;
    private String powerSteeringFluidRemarks;
    private String brakeFluidSpecs;
    private String brakeFluidVolume;
    private String brakeFluidRemarks;
    private String washerFluidRemarks;
    private double latitude;
    private double longitude;
    private String imageURL;
    private String certifyingCompany;
    private int numberOfTires;
    private String tireOne;
    private String tireTwo;
    private String tireThree;
    private String tireFour;
    private String tireFive;
    private String tireSix;
    private String tireSeven;
    private String tireEight;
    private String tireNine;
    private String tireTen;
    private String tireEleven;
    private String tireTwelve;
    private String tireThirteen;
    private String tireFourteen;
    private String tireFifteen;
    private String tireSixteen;
    private String tireSeventeen;
    private String tireEighteen;
    private String tireNineteen;
    private String tireTwenty;
    private String tireTwentyOne;
    private String tireTwentyTwo;
    private String tireSpare;
    private String status;
    private String certificationStatus;
    private String maintenanceStatus;
    private String registrationStatus;
    private String insuranceStatus;

    String pattern = "dd/MM/yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    Calendar todayCalendar = Calendar.getInstance();
    Calendar oneWeekAheadCalendar = Calendar.getInstance();


    public Vehicle() {
    }
    
    public Vehicle(JSONObject response)throws JSONException{
        setEquipmentID(response.getString("equipmentID"));
        setOperationalStatus(response.getString("operationalStatus"));
        setAssetType(response.getString("assetType"));
        setAssetCode(response.getString("assetCode"));
        setSerialNumber(response.getString("serialNumber"));
        setPlateNumber(response.getString("plateNumber"));
        setProvinceOfRegistration(response.getString("provinceOfRegistration"));
        setAssetOwnerName(response.getString("assetOwnerName"));
        setAssetOwnerPhoneNumber(response.getString("assetOwnerPhoneNumber"));
        setAssetOwnerEmail(response.getString("assetOwnerEmail"));
        setAssetOwnerLineID(response.getString("assetOwnerLineID"));
        setDriver(response.getString("driver"));
        setDriverTrainingExpiryDate(response.getString("driverTrainingExpiryDate"));
        setPhone(response.getString("phone"));
        setCertificationExpiryDate(response.getString("certificationExpiryDate"));
        setCertificationPerformedBy(response.getString("certificationPerformedBy"));
        setMaintenanceExpiryDate(response.getString("maintenanceExpiryDate"));
        setMaintenancePerformedBy(response.getString("maintenancePerformedBy"));
        setRegistrationExpiryDate(response.getString("registrationExpiryDate"));
        setRegistrationPerformedBy(response.getString("registrationPerformedBy"));
        setInsuranceExpiryDate(response.getString("insuranceExpiryDate"));
        setInsurancePerformedBy(response.getString("insurancePerformedBy"));
        setCompany(response.getString("company"));
        setMaker(response.getString("maker"));
        setOdometer(Integer.parseInt(response.getString("odometer")));
        setVin(response.getString("vin"));
        setEngineNumber(response.getString("engineNumber"));
        setBelt(response.getString("belt"));
        setHose(response.getString("hose"));
        setOilLeak(response.getString("oilLeak"));
        setAircon(response.getString("aircon"));
        setWiper(response.getString("wiper"));
        setHeadHi(response.getString("headHi"));
        setHeadLo(response.getString("headLo"));
        setDrivingLights(response.getString("drivingLights"));
        setTurnSignal(response.getString("turnSignal"));
        setBrakeLights(response.getString("brakeLights"));
        setHazardLights(response.getString("hazardLights"));
        setDoorLocks(response.getString("doorLocks"));
        setWindshield(response.getString("windshield"));
        setWindows(response.getString("windows"));
        setRadio(response.getString("radio"));
        setHorn(response.getString("horn"));
        setFireExtinguisher(response.getString("fireExtinguisher"));
        setFirstAid(response.getString("firstAid"));
        setCoolant(response.getString("coolant"));
        setEngineOil(response.getString("engineOil"));
        setTransmissionOil(response.getString("transmissionOil"));
        setPowerSteeringFluid(response.getString("powerSteeringFluid"));
        setBrakeFluid(response.getString("brakeFluid"));
        setWasherFluid(response.getString("washerFluid"));
        setBeltRemarks(response.getString("beltRemarks"));
        setHoseRemarks(response.getString("hoseRemarks"));
        setOilLeakRemarks(response.getString("oilLeakRemarks"));
        setAirconRemarks(response.getString("airconRemarks"));
        setWiperRemarks(response.getString("wiperRemarks"));
        setHeadHiRemarks(response.getString("headHiRemarks"));
        setHeadLoRemarks(response.getString("headLoRemarks"));
        setDrivingLightsRemarks(response.getString("drivingLightsRemarks"));
        setTurnSignalRemarks(response.getString("turnSignalRemarks"));
        setBrakeLightsRemarks(response.getString("brakeLightsRemarks"));
        setHazardLightsRemarks(response.getString("hazardLightsRemarks"));
        setDoorLocksRemarks(response.getString("doorLocksRemarks"));
        setWindshieldRemarks(response.getString("windshieldRemarks"));
        setWindowsRemarks(response.getString("windowsRemarks"));
        setRadioRemarks(response.getString("radioRemarks"));
        setHornRemarks(response.getString("hornRemarks"));
        setFireExtinguisherRemarks(response.getString("fireExtinguisherRemarks"));
        setFirstAidRemarks(response.getString("firstAidRemarks"));
        setCoolantSpecs(response.getString("coolantSpecs"));
        setCoolantVolume(response.getString("coolantVolume"));
        setCoolantRemarks(response.getString("coolantRemarks"));
        setEngineOilSpecs(response.getString("engineOilSpecs"));
        setEngineOilVolume(response.getString("engineOilVolume"));
        setEngineOilRemarks(response.getString("engineOilRemarks"));
        setTransmissionOilSpecs(response.getString("transmissionOilSpecs"));
        setTransmissionOilVolume(response.getString("transmissionOilVolume"));
        setTransmissionOilRemarks(response.getString("transmissionOilRemarks"));
        setPowerSteeringFluidSpecs(response.getString("powerSteeringFluidSpecs"));
        setPowerSteeringFluidVolume(response.getString("powerSteeringFluidVolume"));
        setPowerSteeringFluidRemarks(response.getString("powerSteeringFluidRemarks"));
        setBrakeFluidSpecs(response.getString("brakeFluidSpecs"));
        setBrakeFluidVolume(response.getString("brakeFluidVolume"));
        setBrakeFluidRemarks(response.getString("brakeFluidRemarks"));
        setWasherFluidRemarks(response.getString("washerFluidRemarks"));
        setLatitude(Double.parseDouble(response.getString("latitude")));
        setLongitude(Double.parseDouble(response.getString("longitude")));
        setImageURL(response.getString("imageURL"));
        setCertifyingCompany(response.getString("certifyingCompany"));
        setNumberOfTires(Integer.parseInt(response.getString("numberOfTires")));
        setTireOne(response.getString("tireOne"));
        setTireTwo(response.getString("tireTwo"));
        setTireThree(response.getString("tireThree"));
        setTireFour(response.getString("tireFour"));
        setTireFive(response.getString("tireFive"));
        setTireSix(response.getString("tireSix"));
        setTireSeven(response.getString("tireSeven"));
        setTireEight(response.getString("tireEight"));
        setTireNine(response.getString("tireNine"));
        setTireTen(response.getString("tireTen"));
        setTireEleven(response.getString("tireEleven"));
        setTireTwelve(response.getString("tireTwelve"));
        setTireThirteen(response.getString("tireThirteen"));
        setTireFourteen(response.getString("tireFourteen"));
        setTireFifteen(response.getString("tireFifteen"));
        setTireSixteen(response.getString("tireSixteen"));
        setTireSeventeen(response.getString("tireSeventeen"));
        setTireEighteen(response.getString("tireEighteen"));
        setTireNineteen(response.getString("tireNineteen"));
        setTireTwenty(response.getString("tireTwenty"));
        setTireTwentyOne(response.getString("tireTwentyOne"));
        setTireTwentyTwo(response.getString("tireTwentyTwo"));
        setTireSpare(response.getString("tireSpare"));
    }

    public void checkStatus() {

        String pattern = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar todayCalendar = Calendar.getInstance();
        Calendar oneWeekAheadCalendar = Calendar.getInstance();
        oneWeekAheadCalendar.add(Calendar.DAY_OF_YEAR, 7);

        String pToday = sdf.format(todayCalendar.getTime());
        String pOneWeekAhead = sdf.format(oneWeekAheadCalendar.getTime());
        Date dateCertificationExpiryDate = null;
        Date dateMaintenanceExpiryDate = null;
        Date dateRegistrationExpiryDate = null;
        Date dateInsuranceExpiryDate = null;
        Date today = null;
        Date oneWeekAhead = null;

        try {
            dateCertificationExpiryDate = sdf.parse(certificationExpiryDate);
            dateMaintenanceExpiryDate = sdf.parse(maintenanceExpiryDate);
            dateRegistrationExpiryDate = sdf.parse(registrationExpiryDate);
            dateInsuranceExpiryDate = sdf.parse(insuranceExpiryDate);
            today = sdf.parse(pToday);
            oneWeekAhead = sdf.parse(pOneWeekAhead);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert dateCertificationExpiryDate != null;
        assert dateMaintenanceExpiryDate != null;

        assert today != null;
        assert oneWeekAhead != null;
        assert dateRegistrationExpiryDate != null;
        assert dateInsuranceExpiryDate != null;

        //Check Certification Status
        if (dateCertificationExpiryDate.after(oneWeekAhead) || dateCertificationExpiryDate.equals(oneWeekAhead)) {
            this.certificationStatus = "READY";
        } else if (dateCertificationExpiryDate.before(oneWeekAhead) && dateCertificationExpiryDate.after(today)) {
            this.certificationStatus = "WARNING";
        } else certificationStatus = "NOT READY";

        //Check Maintenance Status
        if (dateMaintenanceExpiryDate.after(oneWeekAhead) || dateMaintenanceExpiryDate.equals(oneWeekAhead)) {
            this.maintenanceStatus = "READY";
        } else if (dateMaintenanceExpiryDate.before(oneWeekAhead) && dateMaintenanceExpiryDate.after(today)) {
            this.maintenanceStatus = "WARNING";
        } else maintenanceStatus = "NOT READY";

        //Check Registration Status
        if (dateRegistrationExpiryDate.after(oneWeekAhead) || dateRegistrationExpiryDate.equals(oneWeekAhead)) {
            this.registrationStatus = "READY";
        } else if (dateRegistrationExpiryDate.before(oneWeekAhead) && dateRegistrationExpiryDate.after(today)) {
            this.registrationStatus = "WARNING";
        } else registrationStatus = "NOT READY";

        //Check Insurance Status
        if (dateInsuranceExpiryDate.after(oneWeekAhead) || dateInsuranceExpiryDate.equals(oneWeekAhead)) {
            this.insuranceStatus = "READY";
        } else if (dateInsuranceExpiryDate.before(oneWeekAhead) && dateInsuranceExpiryDate.after(today)) {
            this.insuranceStatus = "WARNING";
        } else insuranceStatus = "NOT READY";

        //Set Status
        if (certificationStatus == "READY" && maintenanceStatus == "READY" && registrationStatus == "READY" && insuranceStatus == "READY") {
            setStatus("READY");
        } else if (certificationStatus == "WARNING" || maintenanceStatus == "WARNING" || registrationStatus == "WARNING" || insuranceStatus == "WARNING") {
            setStatus("WARNING");
        } else setStatus("NOT READY");
    }

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

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getProvinceOfRegistration() {
        return provinceOfRegistration;
    }

    public void setProvinceOfRegistration(String provinceOfRegistration) {
        this.provinceOfRegistration = provinceOfRegistration;
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

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDriverTrainingExpiryDate() {
        return driverTrainingExpiryDate;
    }

    public void setDriverTrainingExpiryDate(String driverTrainingExpiryDate) {
        this.driverTrainingExpiryDate = driverTrainingExpiryDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCertificationExpiryDate() {
        return certificationExpiryDate;
    }

    public void setCertificationExpiryDate(String certificationExpiryDate) {
        this.certificationExpiryDate = certificationExpiryDate;
    }

    public String getCertificationPerformedBy() {
        return certificationPerformedBy;
    }

    public void setCertificationPerformedBy(String certificationPerformedBy) {
        this.certificationPerformedBy = certificationPerformedBy;
    }

    public String getMaintenanceExpiryDate() {
        return maintenanceExpiryDate;
    }

    public void setMaintenanceExpiryDate(String maintenanceExpiryDate) {
        this.maintenanceExpiryDate = maintenanceExpiryDate;
    }

    public String getMaintenancePerformedBy() {
        return maintenancePerformedBy;
    }

    public void setMaintenancePerformedBy(String maintenancePerformedBy) {
        this.maintenancePerformedBy = maintenancePerformedBy;
    }

    public String getRegistrationExpiryDate() {
        return registrationExpiryDate;
    }

    public void setRegistrationExpiryDate(String registrationExpiryDate) {
        this.registrationExpiryDate = registrationExpiryDate;
    }

    public String getRegistrationPerformedBy() {
        return registrationPerformedBy;
    }

    public void setRegistrationPerformedBy(String registrationPerformedBy) {
        this.registrationPerformedBy = registrationPerformedBy;
    }

    public String getInsuranceExpiryDate() {
        return insuranceExpiryDate;
    }

    public void setInsuranceExpiryDate(String insuranceExpiryDate) {
        this.insuranceExpiryDate = insuranceExpiryDate;
    }

    public String getInsurancePerformedBy() {
        return insurancePerformedBy;
    }

    public void setInsurancePerformedBy(String insurancePerformedBy) {
        this.insurancePerformedBy = insurancePerformedBy;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public int getOdometer() {
        return odometer;
    }

    public void setOdometer(int odometer) {
        this.odometer = odometer;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public String getBelt() {
        return belt;
    }

    public void setBelt(String belt) {
        this.belt = belt;
    }

    public String getHose() {
        return hose;
    }

    public void setHose(String hose) {
        this.hose = hose;
    }

    public String getOilLeak() {
        return oilLeak;
    }

    public void setOilLeak(String oilLeak) {
        this.oilLeak = oilLeak;
    }

    public String getAircon() {
        return aircon;
    }

    public void setAircon(String aircon) {
        this.aircon = aircon;
    }

    public String getWiper() {
        return wiper;
    }

    public void setWiper(String wiper) {
        this.wiper = wiper;
    }

    public String getHeadHi() {
        return headHi;
    }

    public void setHeadHi(String headHi) {
        this.headHi = headHi;
    }

    public String getHeadLo() {
        return headLo;
    }

    public void setHeadLo(String headLo) {
        this.headLo = headLo;
    }

    public String getDrivingLights() {
        return drivingLights;
    }

    public void setDrivingLights(String drivingLights) {
        this.drivingLights = drivingLights;
    }

    public String getTurnSignal() {
        return turnSignal;
    }

    public void setTurnSignal(String turnSignal) {
        this.turnSignal = turnSignal;
    }

    public String getBrakeLights() {
        return brakeLights;
    }

    public void setBrakeLights(String brakeLights) {
        this.brakeLights = brakeLights;
    }

    public String getHazardLights() {
        return hazardLights;
    }

    public void setHazardLights(String hazardLights) {
        this.hazardLights = hazardLights;
    }

    public String getDoorLocks() {
        return doorLocks;
    }

    public void setDoorLocks(String doorLocks) {
        this.doorLocks = doorLocks;
    }

    public String getWindshield() {
        return windshield;
    }

    public void setWindshield(String windshield) {
        this.windshield = windshield;
    }

    public String getWindows() {
        return windows;
    }

    public void setWindows(String windows) {
        this.windows = windows;
    }

    public String getRadio() {
        return radio;
    }

    public void setRadio(String radio) {
        this.radio = radio;
    }

    public String getHorn() {
        return horn;
    }

    public void setHorn(String horn) {
        this.horn = horn;
    }

    public String getFireExtinguisher() {
        return fireExtinguisher;
    }

    public void setFireExtinguisher(String fireExtinguisher) {
        this.fireExtinguisher = fireExtinguisher;
    }

    public String getFirstAid() {
        return firstAid;
    }

    public void setFirstAid(String firstAid) {
        this.firstAid = firstAid;
    }

    public String getCoolant() {
        return coolant;
    }

    public void setCoolant(String coolant) {
        this.coolant = coolant;
    }

    public String getEngineOil() {
        return engineOil;
    }

    public void setEngineOil(String engineOil) {
        this.engineOil = engineOil;
    }

    public String getTransmissionOil() {
        return transmissionOil;
    }

    public void setTransmissionOil(String transmissionOil) {
        this.transmissionOil = transmissionOil;
    }

    public String getPowerSteeringFluid() {
        return powerSteeringFluid;
    }

    public void setPowerSteeringFluid(String powerSteeringFluid) {
        this.powerSteeringFluid = powerSteeringFluid;
    }

    public String getBrakeFluid() {
        return brakeFluid;
    }

    public void setBrakeFluid(String brakeFluid) {
        this.brakeFluid = brakeFluid;
    }

    public String getWasherFluid() {
        return washerFluid;
    }

    public void setWasherFluid(String washerFluid) {
        this.washerFluid = washerFluid;
    }

    public String getBeltRemarks() {
        return beltRemarks;
    }

    public void setBeltRemarks(String beltRemarks) {
        this.beltRemarks = beltRemarks;
    }

    public String getHoseRemarks() {
        return hoseRemarks;
    }

    public void setHoseRemarks(String hoseRemarks) {
        this.hoseRemarks = hoseRemarks;
    }

    public String getOilLeakRemarks() {
        return oilLeakRemarks;
    }

    public void setOilLeakRemarks(String oilLeakRemarks) {
        this.oilLeakRemarks = oilLeakRemarks;
    }

    public String getAirconRemarks() {
        return airconRemarks;
    }

    public void setAirconRemarks(String airconRemarks) {
        this.airconRemarks = airconRemarks;
    }

    public String getWiperRemarks() {
        return wiperRemarks;
    }

    public void setWiperRemarks(String wiperRemarks) {
        this.wiperRemarks = wiperRemarks;
    }

    public String getHeadHiRemarks() {
        return headHiRemarks;
    }

    public void setHeadHiRemarks(String headHiRemarks) {
        this.headHiRemarks = headHiRemarks;
    }

    public String getHeadLoRemarks() {
        return headLoRemarks;
    }

    public void setHeadLoRemarks(String headLoRemarks) {
        this.headLoRemarks = headLoRemarks;
    }

    public String getDrivingLightsRemarks() {
        return drivingLightsRemarks;
    }

    public void setDrivingLightsRemarks(String drivingLightsRemarks) {
        this.drivingLightsRemarks = drivingLightsRemarks;
    }

    public String getTurnSignalRemarks() {
        return turnSignalRemarks;
    }

    public void setTurnSignalRemarks(String turnSignalRemarks) {
        this.turnSignalRemarks = turnSignalRemarks;
    }

    public String getBrakeLightsRemarks() {
        return brakeLightsRemarks;
    }

    public void setBrakeLightsRemarks(String brakeLightsRemarks) {
        this.brakeLightsRemarks = brakeLightsRemarks;
    }

    public String getHazardLightsRemarks() {
        return hazardLightsRemarks;
    }

    public void setHazardLightsRemarks(String hazardLightsRemarks) {
        this.hazardLightsRemarks = hazardLightsRemarks;
    }

    public String getDoorLocksRemarks() {
        return doorLocksRemarks;
    }

    public void setDoorLocksRemarks(String doorLocksRemarks) {
        this.doorLocksRemarks = doorLocksRemarks;
    }

    public String getWindshieldRemarks() {
        return windshieldRemarks;
    }

    public void setWindshieldRemarks(String windshieldRemarks) {
        this.windshieldRemarks = windshieldRemarks;
    }

    public String getWindowsRemarks() {
        return windowsRemarks;
    }

    public void setWindowsRemarks(String windowsRemarks) {
        this.windowsRemarks = windowsRemarks;
    }

    public String getRadioRemarks() {
        return radioRemarks;
    }

    public void setRadioRemarks(String radioRemarks) {
        this.radioRemarks = radioRemarks;
    }

    public String getHornRemarks() {
        return hornRemarks;
    }

    public void setHornRemarks(String hornRemarks) {
        this.hornRemarks = hornRemarks;
    }

    public String getFireExtinguisherRemarks() {
        return fireExtinguisherRemarks;
    }

    public void setFireExtinguisherRemarks(String fireExtinguisherRemarks) {
        this.fireExtinguisherRemarks = fireExtinguisherRemarks;
    }

    public String getFirstAidRemarks() {
        return firstAidRemarks;
    }

    public void setFirstAidRemarks(String firstAidRemarks) {
        this.firstAidRemarks = firstAidRemarks;
    }

    public String getCoolantSpecs() {
        return coolantSpecs;
    }

    public void setCoolantSpecs(String coolantSpecs) {
        this.coolantSpecs = coolantSpecs;
    }

    public String getCoolantVolume() {
        return coolantVolume;
    }

    public void setCoolantVolume(String coolantVolume) {
        this.coolantVolume = coolantVolume;
    }

    public String getCoolantRemarks() {
        return coolantRemarks;
    }

    public void setCoolantRemarks(String coolantRemarks) {
        this.coolantRemarks = coolantRemarks;
    }

    public String getEngineOilSpecs() {
        return engineOilSpecs;
    }

    public void setEngineOilSpecs(String engineOilSpecs) {
        this.engineOilSpecs = engineOilSpecs;
    }

    public String getEngineOilVolume() {
        return engineOilVolume;
    }

    public void setEngineOilVolume(String engineOilVolume) {
        this.engineOilVolume = engineOilVolume;
    }

    public String getEngineOilRemarks() {
        return engineOilRemarks;
    }

    public void setEngineOilRemarks(String engineOilRemarks) {
        this.engineOilRemarks = engineOilRemarks;
    }

    public String getTransmissionOilSpecs() {
        return transmissionOilSpecs;
    }

    public void setTransmissionOilSpecs(String transmissionOilSpecs) {
        this.transmissionOilSpecs = transmissionOilSpecs;
    }

    public String getTransmissionOilVolume() {
        return transmissionOilVolume;
    }

    public void setTransmissionOilVolume(String transmissionOilVolume) {
        this.transmissionOilVolume = transmissionOilVolume;
    }

    public String getTransmissionOilRemarks() {
        return transmissionOilRemarks;
    }

    public void setTransmissionOilRemarks(String transmissionOilRemarks) {
        this.transmissionOilRemarks = transmissionOilRemarks;
    }

    public String getPowerSteeringFluidSpecs() {
        return powerSteeringFluidSpecs;
    }

    public void setPowerSteeringFluidSpecs(String powerSteeringFluidSpecs) {
        this.powerSteeringFluidSpecs = powerSteeringFluidSpecs;
    }

    public String getPowerSteeringFluidVolume() {
        return powerSteeringFluidVolume;
    }

    public void setPowerSteeringFluidVolume(String powerSteeringFluidVolume) {
        this.powerSteeringFluidVolume = powerSteeringFluidVolume;
    }

    public String getPowerSteeringFluidRemarks() {
        return powerSteeringFluidRemarks;
    }

    public void setPowerSteeringFluidRemarks(String powerSteeringFluidRemarks) {
        this.powerSteeringFluidRemarks = powerSteeringFluidRemarks;
    }

    public String getBrakeFluidSpecs() {
        return brakeFluidSpecs;
    }

    public void setBrakeFluidSpecs(String brakeFluidSpecs) {
        this.brakeFluidSpecs = brakeFluidSpecs;
    }

    public String getBrakeFluidVolume() {
        return brakeFluidVolume;
    }

    public void setBrakeFluidVolume(String brakeFluidVolume) {
        this.brakeFluidVolume = brakeFluidVolume;
    }

    public String getBrakeFluidRemarks() {
        return brakeFluidRemarks;
    }

    public void setBrakeFluidRemarks(String brakeFluidRemarks) {
        this.brakeFluidRemarks = brakeFluidRemarks;
    }

    public String getWasherFluidRemarks() {
        return washerFluidRemarks;
    }

    public void setWasherFluidRemarks(String washerFluidRemarks) {
        this.washerFluidRemarks = washerFluidRemarks;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCertifyingCompany() {
        return certifyingCompany;
    }

    public void setCertifyingCompany(String certifyingCompany) {
        this.certifyingCompany = certifyingCompany;
    }

    public int getNumberOfTires() {
        return numberOfTires;
    }

    public void setNumberOfTires(int numberOfTires) {
        this.numberOfTires = numberOfTires;
    }

    public String getTireOne() {
        return tireOne;
    }

    public void setTireOne(String tireOne) {
        if (tireOne == null) {
            this.tireOne = "";
        } else this.tireOne = tireOne;
    }

    public String getTireTwo() {
        return tireTwo;
    }

    public void setTireTwo(String tireTwo) {
        if (tireTwo == null) {
            this.tireTwo = "";
        } else this.tireTwo = tireTwo;
    }

    public String getTireThree() {
        return tireThree;
    }

    public void setTireThree(String tireThree) {
        if (tireThree == null) {
            this.tireThree = "";
        } else this.tireThree = tireThree;
    }

    public String getTireFour() {
        return tireFour;
    }

    public void setTireFour(String tireFour) {
        if (tireFour == null) {
            this.tireFour = "";
        } else this.tireFour = tireFour;
    }

    public String getTireFive() {
        return tireFive;
    }

    public void setTireFive(String tireFive) {
        this.tireFive = tireFive;
    }

    public String getTireSix() {
        return tireSix;
    }

    public void setTireSix(String tireSix) {
        this.tireSix = tireSix;
    }

    public String getTireSeven() {
        return tireSeven;
    }

    public void setTireSeven(String tireSeven) {
        this.tireSeven = tireSeven;
    }

    public String getTireEight() {
        return tireEight;
    }

    public void setTireEight(String tireEight) {
        this.tireEight = tireEight;
    }

    public String getTireNine() {
        return tireNine;
    }

    public void setTireNine(String tireNine) {
        this.tireNine = tireNine;
    }

    public String getTireTen() {
        return tireTen;
    }

    public void setTireTen(String tireTen) {
        this.tireTen = tireTen;
    }

    public String getTireEleven() {
        return tireEleven;
    }

    public void setTireEleven(String tireEleven) {
        this.tireEleven = tireEleven;
    }

    public String getTireTwelve() {
        return tireTwelve;
    }

    public void setTireTwelve(String tireTwelve) {
        this.tireTwelve = tireTwelve;
    }

    public String getTireThirteen() {
        return tireThirteen;
    }

    public void setTireThirteen(String tireThirteen) {
        this.tireThirteen = tireThirteen;
    }

    public String getTireFourteen() {
        return tireFourteen;
    }

    public void setTireFourteen(String tireFourteen) {
        this.tireFourteen = tireFourteen;
    }

    public String getTireFifteen() {
        return tireFifteen;
    }

    public void setTireFifteen(String tireFifteen) {
        this.tireFifteen = tireFifteen;
    }

    public String getTireSixteen() {
        return tireSixteen;
    }

    public void setTireSixteen(String tireSixteen) {
        this.tireSixteen = tireSixteen;
    }

    public String getTireSeventeen() {
        return tireSeventeen;
    }

    public void setTireSeventeen(String tireSeventeen) {
        this.tireSeventeen = tireSeventeen;
    }

    public String getTireEighteen() {
        return tireEighteen;
    }

    public void setTireEighteen(String tireEighteen) {
        this.tireEighteen = tireEighteen;
    }

    public String getTireNineteen() {
        return tireNineteen;
    }

    public void setTireNineteen(String tireNineteen) {
        this.tireNineteen = tireNineteen;
    }

    public String getTireTwenty() {
        return tireTwenty;
    }

    public void setTireTwenty(String tireTwenty) {
        this.tireTwenty = tireTwenty;
    }

    public String getTireTwentyOne() {
        return tireTwentyOne;
    }

    public void setTireTwentyOne(String tireTwentyOne) {
        this.tireTwentyOne = tireTwentyOne;
    }

    public String getTireTwentyTwo() {
        return tireTwentyTwo;
    }

    public void setTireTwentyTwo(String tireTwentyTwo) {
        this.tireTwentyTwo = tireTwentyTwo;
    }

    public String getTireSpare() {
        return tireSpare;
    }

    public void setTireSpare(String tireSpare) {
        if (tireSpare == null) {
            this.tireSpare = "";
        } else this.tireSpare = tireSpare;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCertificationStatus() {
        return certificationStatus;
    }

    public String getMaintenanceStatus() {
        return maintenanceStatus;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public String getInsuranceStatus() {
        return insuranceStatus;
    }
}

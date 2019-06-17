package e.sanjay.kangaroorooms;

public class ParkingAddress {

    String Address;
    String VendorName;

    public String getNoOfVehiclesWithCurrentVendor() {
        return noOfVehiclesWithCurrentVendor;
    }

    public void setNoOfVehiclesWithCurrentVendor(String noOfVehiclesWithCurrentVendor) {
        this.noOfVehiclesWithCurrentVendor = noOfVehiclesWithCurrentVendor;
    }

    String noOfVehiclesWithCurrentVendor;

    public ParkingAddress(){};

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getVendorName() {
        return VendorName;
    }

    public void setVendorName(String vendorName) {
        VendorName = vendorName;
    }
}

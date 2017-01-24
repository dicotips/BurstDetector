package cl.uchile.dcc.events.twitter;

import twitter4j.Place;

/**
* This Class contains the Place data of a tweet if it has the Geo-location
* enabled. This entry will be inserted into the database.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public class TPlace {
  /**
   * ID of the Place assigned by Twitter.
   */
  private String id;
  
  /**
   * Name of the Place GeoLocated by Twitter.
   */
  private String name;
  
  /**
   * Country Name of the Place GeoLocated by Twitter.
   */
  private String country;
  
  /**
   * Country ISO-code of the Place GeoLocated by Twitter.
   */
  private String countryCode;
  
  /**
   * Full Name of the Place GeoLocated by Twitter (Neightborhood, City).
   */
  private String fullName;
  
  /**
   * Street Address of the Place GeoLocated by Twitter.
   */
  private String streetAddress;
  
  /**
   * URL of the Place GeoLocated by Twitter.
   */
  private String url;
  
  /**
   * Type of the Place GeoLocated by Twitter.
   */
  private String placeType;
  
  /**
   * Constructor that contains all the parameters of the Place GeoLocated in a Tweet.
   * @param place Place of a Tweet status.
   */
  public TPlace(Place place){
    this.id           = place.getId();
    this.name         = place.getName();
    this.country      = place.getCountry();
    this.countryCode  = place.getCountryCode();
    this.fullName     = place.getFullName();
    this.streetAddress= place.getStreetAddress();
    this.url          = place.getURL();
    this.placeType    = place.getPlaceType();
  }
  
  /**
   * Constructor that contains all the parameters of the Place GeoLocated in a Tweet.
   * @param _id
   * @param _name
   * @param _country
   * @param _country_code
   * @param _fullname
   * @param _street_address
   * @param _url
   * @param _place_type 
   */
  public TPlace(
    String _id,
    String _name,
    String _country,
    String _country_code,
    String _fullname,
    String _street_address,
    String _url,
    String _place_type
  ){
    this.id           = _id;
    this.name         = _name;
    this.country      = _country;
    this.countryCode  = _country_code;
    this.fullName     = _fullname;
    this.streetAddress= _street_address;
    this.url          = _url;
    this.placeType    = _place_type;
  }
  
  /**
   * Gets the ID of the Place of the Tweet.
   * @return Returns the ID of the Place location of the Tweet.
   */
  public String getId(){
    return id;
  }
  
  /**
   * Gets the Name of the Place location of the Tweet.
   * @return Returns the Name of the Place location of the Tweet.
   */
  public String getName(){
    return name;
  }
  
  /**
   * Gets the Country name of the Place location of the Tweet.
   * @return Returns the Country name of the Place location of the Tweet.
   */
  public String getCountry(){
    return country;
  }
  
  /**
   * Gets the ISO Code of the Country of the Place location of the Tweet.
   * @return Returns the ISO Code of the Country of the Place location of the Tweet.
   */
  public String getCountryCode(){
    return countryCode;
  }
  
  /**
   * Gets the Full Name of the Place GeoLocated by Twitter.
   * @return Returns the Full Name of the Place GeoLocated by Twitter.
   */
  public String getFullName(){
    return fullName;
  }
  
  /**
   * Gets the Street Address of the Place GeoLocated by Twitter.
   * @return Returns the Street Address of the Place GeoLocated by Twitter.
   */
  public String getStreetAddress(){
    return streetAddress;
  }

  /**
   * Gets the URL Address of the Place GeoLocated by Twitter.
   * @return Returns the URL Address of the Place GeoLocated by Twitter.
   */
  public String getUrl(){
    return url;
  }

  /**
   * Gets the Type of the Place GeoLocated by Twitter.
   * @return Returns the Type of the Place GeoLocated by Twitter.
   */
  public String getPlaceType(){
    return placeType;
  }
  
  /**
   * Returns a string representation of the Place class.
   * Format: "id  name  country countryCode fullName  streetAddress url placeType"
   * 
   * @return Returns the string representation of the classs.
   */
  @Override
  public String toString(){
    String str =  getId()         +"\t"+
                  getName()       +"\t"+
                  getCountry()    +"\t"+
                  getCountryCode() +"\t"+
                  getFullName()   +"\t"+
                  getStreetAddress() +"\t"+
                  getUrl()           +"\t"+
                  getPlaceType();
    return str;
  }
  
}

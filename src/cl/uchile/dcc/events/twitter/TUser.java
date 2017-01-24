package cl.uchile.dcc.events.twitter;

import cl.uchile.dcc.content.CountryDetector;
import cl.uchile.dcc.utils.TUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import twitter4j.User;

/**
 * This Class contains the data of the User that posted a tweet. This class 
 * encapsulates all the data of the user and his geo-location.
 *
 * @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
 * @version     1.0                 
 * @since       2016-08-17
 */
public final class TUser {
  private static CountryDetector ctry = null;
  
  private long id;
  private String name;
  private Date created_at;
  private String screen_name;
  private String lang;
  private String location;
  private String time_zone;
  private int utc_offset;
  private boolean follow_request_sent;
  private boolean is_translator;
  private boolean contributors_enable;
  private boolean u_protected;
  private boolean geo_enabled;
  private String description;
  private boolean verified;
  private String url;
  private String display_url;
  //private String profile_image_url;
  private String profile_image_url_https;
  private String profile_background_color;
  private String profile_text_color;
  private String profile_link_color;
  private String profile_sidebar_border_color;
  private String profile_sidebar_fill_color;
  private String profile_background_image_url;
  private String profile_background_image_url_https;
  private boolean show_all_inline_media;
  private Set<String> countries_user;

  private long followers_count;
  private long friends_count;
  private long favorites_count;
  private long listed_count;
  private long statuses_count;
  
  private boolean blacklisted = false;

  
  /**
   * 
   */
  public TUser(){
    
    setCreated_at(new Date());
    setId(Long.MAX_VALUE);
    setName("");
    setScreenName("");
    setLang("und");
    setLocation("");
    setTimeZone("");
    setUtcOffset(0);
    setFollowRequestSent(false);
    setIsTranslator(false);
    setContributorsEnable(false);
    setUProtected(false);
    setGeoEnabled(false);
    setProfileBkgImgURLHttps("");
    setDescription("");
    setVerified(false);
    setURL("");
    setProfileImgURL("");
    setProfileBkgColor("");
    setProfileTextColor("");
    setProfileLinkColor("");
    setProfileSidebarBorderColor("");
    setProfileSidebarColor("");
    //setProfileImageURL(usr.getProfileImageURL());
    setProfileBkgImgURL("");
    setDisplayURL("");
    setShowAllInlineMedia(false);
    setFollowersCount(0);
    setFriendsCount(0);
    setFavoritesCount(0);
    setListedCount(0);
    setStatusesCount(0);
    setCountryUser(new HashSet<String>());
    setBlacklisted(false);
  }
  
  /**
   * Constructor of the TUser.
   * @param usr Status.user from the Twitter4J API.
   */
  public TUser(User usr){
    
    setCreated_at(usr.getCreatedAt());
    setId(usr.getId());
    setName(usr.getName());
    setScreenName(usr.getScreenName());
    setLang(usr.getLang());
    setLocation(usr.getLocation());
    setTimeZone(usr.getTimeZone());
    setUtcOffset(usr.getUtcOffset());
    setFollowRequestSent(usr.isFollowRequestSent());
    setIsTranslator(usr.isTranslator());
    setContributorsEnable(usr.isContributorsEnabled());
    setUProtected(usr.isProtected());
    setGeoEnabled(usr.isGeoEnabled());
    setProfileBkgImgURLHttps(usr.getProfileBackgroundImageUrlHttps());
    setDescription(usr.getDescription());
    setVerified(usr.isVerified());
    setURL(usr.getURL());
    setProfileImgURL(usr.getProfileImageURL());
    setProfileBkgColor(usr.getProfileBackgroundColor());
    setProfileTextColor(usr.getProfileTextColor());
    setProfileLinkColor(usr.getProfileLinkColor());
    setProfileSidebarBorderColor(usr.getProfileSidebarBorderColor());
    setProfileSidebarColor(usr.getProfileSidebarFillColor());        
    //setProfileImageURL(usr.getProfileImageURL());
    setProfileBkgImgURL(usr.getProfileBackgroundImageURL());
    setDisplayURL(usr.getOriginalProfileImageURL());
    setShowAllInlineMedia(usr.isShowAllInlineMedia());
    setFollowersCount(usr.getFollowersCount());
    setFriendsCount(usr.getFriendsCount());
    setFavoritesCount(usr.getFavouritesCount());
    setListedCount(usr.getListedCount());
    setStatusesCount(usr.getStatusesCount());
    setBlacklisted(false);
  }
  
  /**
   * Constructor of the TUser.
   * @param _created_at  Sets the Creation Date of the Account of the User (in twitter).
   * @param _id  Sets the User ID.
   * @param _name  Sets the name of the User.
   * @param _screen_name  Sets the Screen Name of the User.
   * @param _lang  Sets the Language of the User (ISO 2 code).
   * @param _location  Sets the location registered by User.
   * @param _time_zone  Sets the time_sone of the location registered by User.
   * @param _utc_offset  Sets the UTC offset of the timezone registered by User.
   * @param _follow_request_sent  Sets if a request of friend was sent.
   * @param _is_translator  Sets true if the user is a translator.
   * @param _contributors_enable  Sets if the user is a contributors.
   * @param _u_protected  Sets if the user status is protected.
   * @param _geo_enabled  Sets true if the geolocation of the user is enabled.
   * @param _profile_background_image_url_https  Sets the Https URL to the image of the background.
   * @param _description  Sets the description.
   * @param _verified  Sets true if the account of the user is verified.
   * @param _url  Sets the URL address of the user.
   * @param _profile_img_url  Sets the URL address of the Profile image of the user.
   * @param _profile_background_color  Sets the color of the background.
   * @param _profile_text_color  Sets the color of the text.
   * @param _profile_link_color  Sets the color of the links.
   * @param _profile_sidebar_border_color  Sets the color of the sidebar border color.
   * @param _profile_sidebar_fill_color  Sets the color of the sidebar fill color.
   * @param _profile_image_url  Sets the URL address of the Profile Image of the user.
   * @param _profile_background_image_url  Sets the Http URL to the image of the background.
   * @param _display_url  Sets the URL address of the Display Image of the user.
   * @param _show_all_inline_media  Sets if the show in an inline media is enabled.
   * @param _followers_count  Sets the number of followers.
   * @param _friends_count  Sets the number of friends.
   * @param _favorites_count  Sets the number of favorite tweets the user has.
   * @param _listed_count  Sets the number of public lists the user is listed on, or -1 if the count is unavailable.
   * @param _statuses_count   Sets the number of tweets the user posted.
   */
  public TUser(
          Date    _created_at,
          long    _id,
          String  _name,
          String  _screen_name,
          String  _lang,
          String  _location,
          String  _time_zone,
          int     _utc_offset,
          boolean _follow_request_sent,
          boolean _is_translator,
          boolean _contributors_enable,
          boolean _u_protected,
          boolean _geo_enabled,
          String  _profile_background_image_url_https,
          String  _description,
          boolean _verified,
          String  _url,
          String  _profile_img_url,
          String  _profile_background_color,
          String  _profile_text_color,
          String  _profile_link_color,
          String  _profile_sidebar_border_color,
          String  _profile_sidebar_fill_color,
          //String  _profile_image_url,
          String  _profile_background_image_url,
          String  _display_url,
          boolean _show_all_inline_media,
          long    _followers_count,
          long    _friends_count,
          long    _favorites_count,
          long    _listed_count,
          long    _statuses_count,
          boolean _blacklisted_user
          ){
    
    setCreated_at(            _created_at);
    setId(                    _id);
    setName(                  _name);
    setScreenName(            _screen_name);
    setLang(                  _lang);
    setLocation(              _location);
    setTimeZone(              _time_zone);
    setUtcOffset(             _utc_offset);
    setFollowRequestSent(     _follow_request_sent);
    setIsTranslator(          _is_translator);
    setContributorsEnable(    _contributors_enable);
    setUProtected(            _u_protected);
    setGeoEnabled(            _geo_enabled);
    setProfileBkgImgURLHttps( _profile_background_image_url_https);
    setDescription(           _description);
    setVerified(              _verified);
    setURL(                   _url);
    setProfileImgURL(         _profile_img_url);
    setProfileBkgColor(       _profile_background_color);
    setProfileTextColor(      _profile_text_color);
    setProfileLinkColor(      _profile_link_color);
    setProfileSidebarBorderColor(_profile_sidebar_border_color);
    setProfileSidebarColor(   _profile_sidebar_fill_color);
    //setProfileImageURL(       _profile_image_url);
    setProfileBkgImgURL(      _profile_background_image_url);
    setDisplayURL(            _display_url);
    setShowAllInlineMedia(    _show_all_inline_media);
    setFollowersCount(        _followers_count);
    setFriendsCount(          _friends_count);
    setFavoritesCount(       _favorites_count);
    setListedCount(           _listed_count);
    setStatusesCount(         _statuses_count);
    setBlacklisted(           _blacklisted_user);
  }

  //////////////////////    SETTERS     //////////////////////
  /**
   * Sets the Creation Date of the Account of the User (in twitter).
   * @param tmp Creation date of the tweet.
   */
  public void setCreated_at(Date tmp){
    created_at = tmp;
  }
  
  /**
   * Sets the User ID.
   * @param vid User ID
   */
  public void setId(long vid){
    this.id = vid;
  }
  
  /**
   * Sets the name of the User.
   * @param nme the name of the User.
   */
  public void setName(String nme){
    name = TUtils.Text_Formatter(nme);
  }
  
  /**
   * Sets the Screen Name of the User.
   * @param scr_name the Screen Name of the User.
   */
  public void setScreenName(String scr_name){
    screen_name = TUtils.Text_Formatter(scr_name);
  }
  
  /**
   * Sets the Language of the User (ISO 2 code).
   * @param lng the language of the User (ISO 2 code).
   */
  public void setLang(String lng){
    lang = lng;
  }
  
  /**
   * Sets the location registered by User.
   * @param loc the location registered by User.
   */
  public void setLocation(String loc){
    location = TUtils.Text_Formatter(loc);
  }
  
  /**
   * Sets the time_sone of the location registered by User.
   * @param tz the time_sone of the location registered by User.
   */
  public void setTimeZone(String tz){
    time_zone = TUtils.Text_Formatter(tz);
  }
  
  /**
   * Sets the UTC offset of the timezone registered by User.
   * @param utc the UTC offset of the timezone registered by User.
   */
  public void setUtcOffset(int utc){
    utc_offset = utc;
  }
  
  /**
   * Sets if a request of friend was sent.
   * @param fr request of friend was sent.
   */
  public void setFollowRequestSent(boolean fr){
    follow_request_sent = fr;
  }
  
  /**
   * Sets true if the user is a translator.
   * @param trans true if the user is a translator
   */
  public void setIsTranslator(boolean trans){
    is_translator = trans;
  }
  
  /**
   * Sets if the user is a contributors.
   * @param ce if the user is enabling contributors
   */
  public void setContributorsEnable(boolean ce){
    contributors_enable = ce;
  }
  
  /**
   * Sets if the user status is protected.
   * @param up true if the user status is protected.
   */
  public void setUProtected(boolean up){
    u_protected = up;
  }
  
  /**
   * Sets true if the geolocation of the user is enabled.
   * @param ge true if the geolocation is enabled.
   */
  public void setGeoEnabled(boolean ge){
    geo_enabled = ge;
  }
  
  /**
   * Sets the Https URL to the image of the background.
   * @param pback the Https URL to the image of the background.
   */
  public void setProfileBkgImgURLHttps(String pback){
    profile_background_image_url_https = pback;
  }
  
  /**
   * Sets the description.
   * @param desc the description.
   */
  public void setDescription(String desc){
    description = TUtils.Text_Formatter(desc);
  }
  
  /**
   * Sets true if the account of the user is verified.
   * @param vf true if the account of the user is verified.
   */
  public void setVerified(boolean vf){
    verified = vf;
  }
  
  /**
   * Sets the URL address of the user.
   * @param vurl the URL address of the user.
   */
  public void setURL(String vurl){
    url = vurl;
  }
  
  /**
   * Sets the URL address of the Profile image of the user.
   * @param prof_img the address URL of the Profile image of the user.
   */
  public void setProfileImgURL(String prof_img){
    profile_image_url_https = prof_img;
  }
  
  /**
   * Sets the color of the background.
   * @param bkg_color the color of the background.
   */
  public void setProfileBkgColor(String bkg_color){
    profile_background_color = bkg_color;
  }
  
  /**
   * Sets the color of the text.
   * @param text_color the color of the text.
   */
  public void setProfileTextColor(String text_color){
    profile_text_color = text_color;
  }
  
  /**
   * Sets the color of the links.
   * @param link_color the color of the links.
   */
  public void setProfileLinkColor(String link_color){
    profile_link_color = link_color;
  }
  
  /**
   * Sets the color of the sidebar border color.
   * @param sidebar_border_color the color of the sidebar border color.
   */
  public void setProfileSidebarBorderColor(String sidebar_border_color){
    profile_sidebar_border_color = sidebar_border_color;        
  }
  
  /**
   * Sets the color of the sidebar fill color.
   * @param sidebar_fill_color the color of the sidebar fill color.
   */
  public void setProfileSidebarColor(String sidebar_fill_color){
    profile_sidebar_fill_color = sidebar_fill_color;
  }
  
  /**
   * Sets the URL address of the Profile Image of the user.
   * @param image_url the URL address of the Profile Image of the user.
   */
  //public void setProfileImageURL(String image_url){
  //  profile_image_url = image_url;
  //}
  
  /**
   * Sets the Http URL to the image of the background.
   * @param background_image_url the Http URL to the image of the background.
   */
  public void setProfileBkgImgURL(String background_image_url){
    profile_background_image_url = background_image_url;
  }
  
  /**
   * Sets the URL address of the Display Image of the user.
   * @param durl the URL address of the Display Image of the user.
   */
  public void setDisplayURL(String durl){
    display_url = durl;
  }
  
  /**
   * Sets if the show in an inline media is enabled.
   * @param all_inline_media true if the show in an inline media is enabled.
   */
  public void setShowAllInlineMedia(boolean all_inline_media){
    show_all_inline_media = all_inline_media;
  }
  
  /**
   * Sets the number of followers.
   * @param cfollowers the number of followers.
   */
  public void setFollowersCount(long cfollowers){
    followers_count = cfollowers;
  }
  
  /**
   * Sets the number of friends.
   * @param cfriends the number of friends.
   */
  public void setFriendsCount(long cfriends){
    friends_count = cfriends;
  }
  
  /**
   * Sets the number of favorite tweets the user has.
   * @param cfavourites the number of favorite tweets the user has.
   */
  public void setFavoritesCount(long cfavorites){
    favorites_count = cfavorites;
  }
  
  /**
   * Sets the number of public lists the user is listed on, 
   * or -1 if the count is unavailable.
   * @param clisted number of public list the user is listed on.
   */
  public void setListedCount(long clisted){
    listed_count = clisted;
  }
  
  /**
   * Sets the number of tweets the user posted.
   * @param stc the number of tweets the user posted.
   */
  public void setStatusesCount(long stc){
    statuses_count = stc;
  }
  
  /**
   * Sets the country detected in the location of the user.
   * @param countries the country detected in the location of the user.
   */
  public void setCountryUser(Set<String> countries){
    countries_user = countries;
  }
  
  /**
   * Sets the user of its blacklisted or not.
   * @param bl true to blacklist the user.
   */
  public void setBlacklisted(boolean bl){
    blacklisted = bl;
  }

  //////////////////////    GETTERS     //////////////////////    
  /**
   * Gets the Creation Date of the Account of the User (in twitter).
   * @return Returns the Creation Date of the tweet.
   */
  public Date     getCreated_at(){
    return created_at;        
  }
  
  /**
   * Returns the ets the User ID.
   * @return` vid User ID.
   */
  public String   getCreated_at_String(){
    return TUtils.Date_Formatter(created_at);
  }
  
  /**
   * Returns the User ID.
   * @return the User ID.
   */
  public long     getId(){
    return id;
  }
  
  /**
   * Returns the name of the User.
   * @return the name of the User.
   */
  public String   getName(){
    return name;
  }
  
  /**
   * Returns the Screen Name of the User.
   * @return the Screen Name of the User.
   */
  public String   getScreenName(){
    return screen_name;
  }
  
  /**
   * Returns the Language of the User (ISO 2 code).
   * @return the Language of the User (ISO 2 code).
   */
  public String   getLang(){
    return lang;
  }
  
  /**
   * Returns the location registered by User.
   * @return the location registered by User.
   */
  public String   getLocation(){
    return location;
  }
  
  /**
   * Returns the time_sone of the location registered by User.
   * @return the time_sone of the location registered by User.
   */
  public String   getTimeZone(){
    return time_zone;
  }
  
  /**
   * Returns the UTC offset of the timezone registered by User.
   * @return the UTC offset of the timezone registered by User.
   */
  public int      getUtcOffset(){
    return utc_offset;
  }
  
  /**
   * Returns if a request of friend was sent.
   * @return if a request of friend was sent.
   */
  public boolean  isFollowRequestSent(){
    return follow_request_sent;
  }
  
  /**
   * Returns true if the user is a translator.
   * @return true if the user is a translator.
   */
  public boolean  isTranslator(){
    return is_translator;
  }
  
  /**
   * Returns if the user is a contributors.
   * @return if the user is a contributors.
   */
  public boolean  isContributorsEnable(){
    return contributors_enable;
  }
  
  /**
   * Returns true if the user status is protected.
   * @return if the user status is protected.
   */
  public boolean  isUProtected(){
    return u_protected;
  }
  
  /**
   * Returns true if the geolocation of the user is enabled.
   * @return true if the geolocation of the user is enabled.
   */
  public boolean  isGeoEnabled(){
    return geo_enabled;
  }
  
  /**
   * Returns the Https URL to the image of the background.
   * @return the Https URL to the image of the background.
   */
  public String   getProfileBkgImgURLHttps(){
    return profile_background_image_url_https;
  }
  
  /**
   * Returns the description.
   * @return the description.
   */
  public String   getDescription(){
    return description;
  }
  
  /**
   * Returns true if the account of the user is verified.
   * @return true if the account of the user is verified.
   */
  public boolean  isVerified(){
    return verified;
  }
  
  /**
   * Returns the URL address of the user.
   * @return the URL address of the user.
   */
  public String   getURL(){
    return url;
  }
  
  /**
   * Returns the URL address of the Profile image of the user.
   * @return the URL address of the Profile image of the user.
   */
  public String   getProfileImgURL(){
    return profile_image_url_https;
  }
  
  /**
   * Returns the color of the background.
   * @return the color of the background.
   */
  public String   getProfileBkgColor(){
    return profile_background_color;
  }
  
  /**
   * Return the color of the text.
   * @return the color of the text.
   */
  public String   getProfileTextColor(){
    return profile_text_color;
  }
  
  /**
   * Returns the color of the links.
   * @return the color of the links.
   */
  public String   getProfileLinkColor(){
    return profile_link_color;
  }
  
  /**
   * Returns the color of the sidebar border color.
   * @return the color of the sidebar border color.
   */
  public String   getProfileSidebarBorderColor(){
    return profile_sidebar_border_color;
  }
  
  /**
   * Returns the color of the sidebar fill color.
   * @return the color of the sidebar fill color.
   */
  public String   getProfileSidebarColor(){
    return profile_sidebar_fill_color;
  }
  
  /**
   * Returns the URL address of the Profile Image of the user.
   * @return the URL address of the Profile Image of the user.
   */
  //public String   getProfileImageURL(){
  //  return profile_image_url;
  //}
  
  /**
   * Returns the Http URL to the image of the background.
   * @return the Http URL to the image of the background.
   */
  public String   getProfileBkgImgURL(){
    return profile_background_image_url;
  }
  
  /**
   * Returns the URL address of the Display Image of the user.
   * @return the URL address of the Display Image of the user.
   */
  public String   getDisplayURL(){
    return display_url;
  }
  
  /**
   * Returns true if the show in an inline media is enabled.
   * @return true if the show in an inline media is enabled.
   */
  public boolean  isShowAllInlineMedia(){
    return show_all_inline_media;
  }
  
  /**
   * Returns the number of followers.
   * @return the number of followers.
   */
  public long     getFollowersCount(){
    return followers_count;
  }
  
  /**
   * Returns the number of friends.
   * @return the number of friends.
   */
  public long     getFriendsCount(){
    return friends_count;
  }
  
  /**
   * Returns the number of favorite tweets the user has.
   * @return the number of favorite tweets the user has.
   */
  public long     getFavoritesCount(){
    return favorites_count;
  }
  
  /**
   * Returns the number of public lists the user is listed on, 
   * or -1 if the count is unavailable.
   * @return number of public list the user is listed on.
   */
  public long     getListedCount(){
    return listed_count;
  }
  
  /**
   * Returns the number of tweets the user posted.
   * @return the number of tweets the user posted.
   */
  public long     getStatusesCount(){
    return statuses_count;
  }
  
  /**
   * Returns the country detected in the location of the user.
   * @return the country detected in the location of the user.
   */
  public Set<String> getCountryUser(){
    return countries_user;
  }
  
  /**
   * Test if the user is blacklisted.
   * @return true if the user is blacklisted.
   */
  public boolean isBlacklisted(){
    return blacklisted;
  }
  
  /**
   * Returns a string representation of the TUser class.
   * 
   * @return Returns the string representation of the classs.
   */
  @Override
  public String toString(){
    String str = getCreated_at_String()      + "\t" +
                 getId()                     + "\t" +
                 getName()                   + "\t" +
                 getScreenName()             + "\t" +
                 Boolean.toString(isBlacklisted()) + "\t" +
                 getLang()                   + "\t" +
                 getLocation()               + "\t" +
                 getTimeZone()                + "\t" +
                 getUtcOffset()              + "\t" +
                 Boolean.toString(isFollowRequestSent())    + "\t" +
                 Boolean.toString(isTranslator())         + "\t" +
                 Boolean.toString(isContributorsEnable())   + "\t" +
                 Boolean.toString(isUProtected())           + "\t" +
                 Boolean.toString(isGeoEnabled())           + "\t" +
                 getProfileBkgImgURLHttps()                  + "\t" +
                 getDescription()                            + "\t" +
                 Boolean.toString(isVerified())             + "\t" +
                 getURL()                    + "\t" +
                 getProfileImgURL()          + "\t" +
                 getProfileBkgColor()        + "\t" +
                 getProfileTextColor()       + "\t" +
                 getProfileLinkColor()       + "\t" +
                 getProfileSidebarBorderColor()      + "\t" +
                 getProfileSidebarColor()    + "\t" +
                 //getProfileImageURL()        + "\t" +
                 getProfileBkgImgURL()       + "\t" +
                 getDisplayURL()             + "\t" +
                 Boolean.toString(isShowAllInlineMedia())   + "\t" +
                 Long.toString(getFollowersCount())          + "\t" +
                 Long.toString(getFriendsCount())            + "\t" +
                 Long.toString(getFavoritesCount())         + "\t" +
                 Long.toString(getListedCount())             + "\t" +
                 Long.toString(getStatusesCount());
    return str;
  }

}
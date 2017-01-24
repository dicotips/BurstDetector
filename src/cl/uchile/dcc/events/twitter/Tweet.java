package cl.uchile.dcc.events.twitter;

import cl.uchile.dcc.content.CountryDetector;
import cl.uchile.dcc.utils.PropertiesTD;
import cl.uchile.dcc.utils.TUtils;
import cl.uchile.dcc.sentiment.Language;
import cl.uchile.dcc.sentiment.SentiStrengthLang;
import cl.uchile.dcc.utils.Blacklist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.UserMentionEntity;
import twitter4j.URLEntity;

/**
* This class encapsulate the Status (tweet) provided by the Twitter4J API. It
* also adds Meta-data, such as Sentiment and GeoLocation detection.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public final class Tweet {
  private static SentiStrengthLang sentLang;
  private static CountryDetector ctry = null;
  
  private Date    downloaded_at;
  private Date    created_at;
  private long    id;
  private TweetType type;
  private String  text;
  private String  language;
  
  private boolean retweet;
  private long    retweet_count;
  private long    id_rt;
  private String  text_rt;
  private boolean blacklisted;
  private boolean isQuote;
  private long    id_quoted;
  private String  text_quoted;
  
  private boolean truncated;
  private boolean favorited;
  private String  source;
  private String  src_href;
  private String  src_rel;
  private String  src_text;
  private long    in_reply_to_user_id;
  private long    in_reply_to_status_id;
  private String  in_reply_to_screen_name;
  private double  geo_latitude;
  private double  geo_longitude;
  private boolean geolocated;
  private boolean has_keyword;
  private boolean repeated_user;
  
  private Set<String> countries_text;

  private TPlace  place;    // PLACE data
  private TUser   tuser;    // USER data
  private TSenti  ss;       // Sentiment Values
  private List<EntityHashTag> HashTags; // Entity HashTags
  private List<EntityURL>     URLs;     // Entity URLs
  private List<EntityUserRef> URefs;    // Entity UserRefs
  private List<EntityMedia>   EMedias;  // Entity Media
  
  private static Blacklist bl;

  
  
  public Tweet(PropertiesTD prop){
    this( new Date(),
          new Date(),
          Long.MAX_VALUE,
          null,
          false,
          false,
          "TWEET",
          "und",
          "",
          false,
          0,
          Long.MAX_VALUE,
          "",
          false,
          Long.MAX_VALUE,
          "",
          "",
          "",
          "",
          Long.MAX_VALUE,
          Long.MAX_VALUE,
          "",
          false,
          -99.999,
          -99.999,
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          new ArrayList<>(),
          new ArrayList<>(),
          new ArrayList<>(),
          new ArrayList<>(),
          prop
        );
    setUser(new TUser());
  }
  
  /**
   * Constructor:  Encapsulates the Status from the Twitter4J into the class Tweet.
   * @param status  Tweet Status from Twitter4J API.
   * @param prop    Parameters from the _setup.txt file
   */
  public Tweet(Status status, PropertiesTD prop) {
    if(ctry == null)
      ctry = new CountryDetector(prop);
            
    downloaded_at = new Date();
    setCreatedAt(status.getCreatedAt());
    setId(status.getId());
    setType(TweetType.TWEET);
    setText(status.getText(), prop);
    setLanguage(status.getLang());
    setRetweet(status.isRetweet());
    setRTCount(status.getRetweetCount());
    setTruncated(status.isTruncated());
    setFavorited(status.isFavorited());
    setSource(status.getSource());
    setIn_reply_to_user_id(status.getInReplyToUserId());
    setIn_reply_to_status_id(status.getInReplyToStatusId());
    setIn_reply_to_screen_name(status.getInReplyToScreenName());
    repeated_user = false;
    countries_text = ctry.DetectCountries(text, language, false);
    
    setGeo(status.getGeoLocation());
    setPlace(status.getPlace());
    //USER data
    if(status.getUser() != null){
      TUser usr = new TUser(status.getUser());
      setUser(usr);
      tuser.setCountryUser(ctry.DetectCountries(tuser.getLocation(), tuser.getLang(), true));
    }
    
    // ENTITIES data
    HashTags  = new ArrayList<>();
    URLs      = new ArrayList<>();
    URefs     = new ArrayList<>();
    EMedias   = new ArrayList<>();

    //HASHTAGS
    for(HashtagEntity hte: status.getHashtagEntities()){
        EntityHashTag ht=new EntityHashTag(hte.getStart(), hte.getEnd(), 
                "#"+hte.getText());
        HashTags.add(ht);
    }

    //URLS
    for(URLEntity eURL: status.getURLEntities()){
      EntityURL vURL=new EntityURL(eURL.getStart(),eURL.getEnd(),
              eURL.getURL(),eURL.getExpandedURL());
      URLs.add(vURL);
    }

    //User References
    for(UserMentionEntity eUR: status.getUserMentionEntities()){
      EntityUserRef vUR=new EntityUserRef(eUR.getStart(), eUR.getEnd(),
              eUR.getId(), "@"+eUR.getScreenName(), eUR.getName());
      URefs.add(vUR);
    }
    
    //Media Entities
    for (MediaEntity me : status.getMediaEntities()) {
      EntityMedia em = new EntityMedia(me.getId(), me.getMediaURL(),
              me.getMediaURLHttps(), me.getType(), me.getSizes());
      EMedias.add(em);
    }
    
    //QUOTED STATUS
    if(status.getQuotedStatus() != null){
      isQuote     = true;
      id_quoted   = status.getQuotedStatusId();
      text_quoted = TUtils.Text_Formatter(status.getQuotedStatus().getText());
    }
    //RT STATUS
    if(status.getRetweetedStatus() != null){
      id_rt   = status.getRetweetedStatus().getId();
      text_rt = TUtils.Text_Formatter(status.getRetweetedStatus().getText());
    }
    
    //Sentiment Analysis
    setSentiment(prop);
    
    //has Keyword
    setHasKeyword(prop);
    
    //Check Blacklists
    if(bl == null){
      bl = new Blacklist(prop);
    }
    tuser.setBlacklisted(bl.containsUser(tuser.getScreenName()));
    blacklisted = bl.containsPhrase(text);
  }
  
  
  /**
   * Constructor:   Encapsulates the inputs of data into a class Tweet.
   */
  public Tweet(
          Date    _download_date,     //
          Date    _creation_date,     //
          long    _id_tweet,           //
          //boolean _blacklisted_tweet,//
          TUser    _user,              //
          //boolean _repeated_user,   //  
          boolean _favorited,         //
          boolean _truncated,         //
          String  _type,              //
          String  _lang_tweet,        //
          String  _text_tweet,        //
          boolean _rt,                //
          long    _rt_count,          //
          long    _rt_id,             //
          String  _text_rt,           //
          boolean _quote,             //
          long    _quote_id,          //
          String  _text_quote,        //
          //boolean _has_keyword,           //
          String  _src_href,                //
          String  _src_rel,                 //
          String  _src_text,                //
          long    _in_reply_to_status_id,   //
          long    _in_reply_to_user_id,     //
          String  _in_reply_to_screen_name, //
          //String  _countries_text,        //
          boolean _geo_located,             //
          double  _geo_latitude,            //
          double  _geo_longitude,           //
          String  _place_id,                //
          String  _place_name,              //
          String  _place_country_code,      //
          String  _place_country,           //
          String  _place_fullname,          //
          String  _place_street_address,    //
          String  _place_url,               //
          String  _place_place_type,        //
          List<EntityHashTag>  _HashTags,
          List<EntityURL>      _URLs,
          List<EntityUserRef>  _URefs,
          List<EntityMedia>    _EMedias,
          //int     _counter,
          PropertiesTD prop) {
    
    if(ctry == null)
      ctry = new CountryDetector(prop);
            
    downloaded_at = _download_date;
    setCreatedAt(_creation_date);
    setId(_id_tweet);
    switch(_type){
      case "QUOTE":   setType(TweetType.QUOTE);   break;
      case "RT_ROOT": setType(TweetType.RT_ROOT); break;
      default:        setType(TweetType.TWEET);   break;
    }
    
    setText(_text_tweet, prop);
    setLanguage(_lang_tweet);
    setRetweet(_rt);
    setRTCount(_rt_count);
    setTruncated(_truncated);
    setFavorited(_favorited);
    setSrcHref(_src_href);
    setSrcRel(_src_rel);
    setSrcText(_src_text);
    setIn_reply_to_user_id(_in_reply_to_user_id);
    setIn_reply_to_status_id(_in_reply_to_status_id);
    setIn_reply_to_screen_name(_in_reply_to_screen_name);
    repeated_user = false;
    countries_text = ctry.DetectCountries(text, language, false);
    geolocated = _geo_located;
    geo_latitude  = _geo_latitude;
    geo_longitude = _geo_longitude;
    if(_place_id == null || _place_id.equals("")){
      place = null;
    }
    else{
      place = new TPlace( _place_id,
                          _place_name,
                          _place_country,
                          _place_country_code,
                          _place_fullname,
                          _place_street_address,
                          _place_url,
                          _place_place_type );
    }
    

    //USER data
    if(_user != null){
      setUser(_user);
      tuser.setCountryUser(ctry.DetectCountries(tuser.getLocation(), tuser.getLang(), true));
    }
    
    // ENTITIES DATA
    if(prop.mysql_src_generate_entities_from_text){
        HashTags  = extractHashTags(getText());
        URefs     = extractUserRef(getText());
        URLs      = extractURLs(getText());
        EMedias   = new ArrayList<>();
    }
    else{
      
      //HASHTAGS
      if(_HashTags == null)
        HashTags  = new ArrayList<>();
      else
        HashTags  = _HashTags;

      //URLS
      if(_URLs == null)
        URLs      = new ArrayList<>();
      else
        URLs      = _URLs;

      //User References
      if(_URefs == null)
        URefs     = new ArrayList<>();
      else
        URefs     = _URefs;

      //Media Entities
      if(_EMedias == null)
        EMedias   = new ArrayList<>();
      else
        EMedias   = _EMedias;

    }

    //QUOTED STATUS
    if(_quote){
      isQuote     = true;
      id_quoted   = _quote_id;
      text_quoted = TUtils.Text_Formatter(_text_quote);
    }
    //RT STATUS
    if(_rt){
      retweet = _rt;
      id_rt   = _rt_id;
      text_rt = TUtils.Text_Formatter(_text_rt);
    }
    
    //Sentiment Analysis
    setSentiment(prop);
    
    //has Keyword
    setHasKeyword(prop);
    
    //Check Blacklists
    if(bl == null){
      bl = new Blacklist(prop);
    }
    if(_user != null){
      setUser(_user);
      tuser.setBlacklisted(bl.containsUser(tuser.getScreenName()));
    }
    blacklisted = bl.containsPhrase(text);
  }
  
  //////////////////////    AUXILIARS   //////////////////////
  /**
   * 
   * @param text
   * @return 
   */
  private List<EntityHashTag> extractHashTags(String text){
    List<EntityHashTag> hashtags = new ArrayList<>();
    
    Pattern pattern = Pattern.compile("#(\\S+)");
    Matcher mat = pattern.matcher(text);
    
    while (mat.find()) {
      hashtags.add(new EntityHashTag(mat.start(), mat.end(), mat.group().replaceAll("[-+.^:,\\?]","")));
    }
    
    return hashtags;
  }
  
  /**
   * 
   * @param text
   * @return 
   */
  private List<EntityUserRef> extractUserRef(String text){
    List<EntityUserRef> urefs = new ArrayList<>();
    
    Pattern pattern = Pattern.compile("@(\\S+)");
    Matcher mat = pattern.matcher(text);
    
    while (mat.find()) {
      urefs.add(new EntityUserRef(mat.start(), mat.end(), 0, mat.group(), mat.group().replaceAll("[-+.^:,\\?]","") ));
    }
    
    return urefs;
  }
  
  /**
   * 
   * @param text
   * @return 
   */
  private List<EntityURL> extractURLs(String text){
    List<EntityURL> urls = new ArrayList<>();
    
    Pattern pattern = Pattern.compile(
            "\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" + 
            "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" + 
            "|mil|biz|info|mobi|name|aero|jobs|museum" + 
            "|travel|[a-z]{2}))(:[\\d]{1,5})?" + 
            "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" + 
            "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
            "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" + 
            "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
            "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" + 
            "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b", Pattern.CASE_INSENSITIVE);
    Matcher mat = pattern.matcher(text);
    
    while (mat.find()) {
      urls.add(new EntityURL(mat.start(), mat.end(), mat.group(), mat.group()));
    }
    
    return urls;
  }
  
  //////////////////////    SETTERS     //////////////////////
  /**
   * Sets the Download Date.
   * @param vDownloadedAt the Download Date.
   */
  public void setDownloadedAt(Date vDownloadedAt){
    created_at = vDownloadedAt;  
  }
  
  /**
   * Sets the Creation Date in format "YYYYMMDD HH:mm:ss Z".
   * @param vCreateDate the Creation Date.
   */
  public void setCreatedAt(Date vCreateDate){
    created_at = vCreateDate;        
  }
  /**
   * Sets the Tweet ID.
   * @param vid the Tweet ID.
   */
  public void setId(long vid){
    id = vid;
  }
  
  /**
   * Sets the type of the Tweet (TWEET, QUOTED, RT_ROOT)
   * @param vtype the type of the Tweet.
   */
  public void setType(TweetType vtype){
    type = vtype;
  }
  
  /**
   * Sets the Text corpus of the tweet.
   * @param msg  Text corpus of the tweet.
   * @param prop Parameters of the  _setup.txt file.
   */
  public void setText(String msg, PropertiesTD prop){
    text = TUtils.Text_Formatter(msg);
    setHasKeyword(prop);
  }
  
  /**
   * Sets the language of the text of the tweet (ISO 2 Language Code - LowerCase).
   * @param Lang the language of the text of the tweet (ISO 2 Language Code - LowerCase).
   */
  public void setLanguage(String Lang) {
    this.language = Lang;
  }
  
  /**
   * Sets true if it is a RT.
   * @param rt true if it is a RT.
   */
  public void setRetweet(boolean rt){
    retweet = rt;
  }
  
  /**
   * Sets true if the user posted again a tweet in the last minutes.
   * @param r_user true if the user posted again a tweet in the last minutes.
   */
  public void setRepeatedUser(boolean r_user){
    repeated_user = r_user;
  }
  
  /**
   * Sets how many times this tweets was retweeted.
   * @param rt_count how many times this tweets was retweeted.
   */
  public void setRTCount(long rt_count){
    retweet_count = rt_count;        
  }
  
  /**
   * Sets true if the text of the tweet is truncated (It happens in RT)
   * @param trunc true if the text of the tweet is truncated.
   */
  public void setTruncated(boolean trunc){
    truncated = trunc;            
  }
  
  /**
   * Sets the tweet as favorited.
   * @param fav the tweet as favorited.
   */
  public void setFavorited(boolean fav){
    favorited = fav;            
  }
  
  /**
   * Sets the source device or site where the tweet was posted.
   * @param src the source device or site where the tweet was posted.
   */
  public void setSource(String src){
    source = src;
    src = src.replaceAll("</a>","").trim();
    src = src.replaceAll("<a href=","").trim();
    src = src.replaceAll("rel=","").trim();
    src = src.replaceAll(">"," ").trim();
    src = src.replaceAll("\"","");
    
    String[] items = src.split(" ");
    int i = 0;
    for (String item : items) {
      switch (i) {
        case 0:
          setSrcHref(items[i]);
          break;
        case 1:
          setSrcRel(items[i]);
          break;
        case 2:
          setSrcText(items[i]);
          break;
        default:
          setSrcText(getSrcText() + " " + items[i]);
          break;
      }
      i++;
    }
  }
  
  /**
   * Sets the HREF source where the tweet was posted.
   * @param src the HREF source where the tweet was posted.
   */
  public void setSrcHref(String src){
    src_href = src;
  }
  
  /**
   * Sets the source_rel of the tweet.
   * @param src the source_rel of the tweet.
   */
  public void setSrcRel(String src){
    src_rel = src;
  }
  
  /**
   * Sets the source device.
   * @param src the source device.
   */
  public void setSrcText(String src){
    src_text = src;
  }
  
  /**
   * Sets the User ID if the tweet is a reply.
   * @param rep_user the User ID if the tweet is a reply.
   */
  public void setIn_reply_to_user_id(long rep_user){
    in_reply_to_user_id = rep_user;
  }
  
  /**
   * Sets the Tweet ID of the replied tweet.
   * @param rep_status the Tweet ID of the replied tweet.
   */
  public void setIn_reply_to_status_id(long rep_status){
    in_reply_to_status_id = rep_status;
  }
  
  /**
   * Sets the User Screen_Name(@) of the user that replied the tweet.
   * @param rep_screen the User Screen_Name(@) of the user that replied the tweet.
   */
  public void setIn_reply_to_screen_name(String rep_screen){
    in_reply_to_screen_name = (rep_screen != null)? "@"+rep_screen: rep_screen;
  }
  
  /**
   * Sets the Geo-Location of the tweet.
   * @param vgeo the Geo-Location of the tweet.
   */
  public void setGeo(GeoLocation vgeo){
    if(vgeo != null){
      geo_latitude  = vgeo.getLatitude();
      geo_longitude = vgeo.getLongitude();
      geolocated = true;
    }
    else{
      geo_latitude  = 0;
      geo_longitude = 0;
      geolocated = false;
    }
  }
  
  /**
   * Sets an instance of the USER of the tweet.
   * @param vuser instance of the USER of the tweet.
   */
  public void setUser(TUser vuser){
    tuser = vuser;
  }
  
  /**
   * Sets the information of the PLACE of the GeoLocation.
   * @param vplace the information of the PLACE of the GeoLocation.
   */
  public void setPlace(Place vplace){
    if(vplace != null){
      this.place = new TPlace(vplace);
    }
  }
  
  /**
   * Sets the list of HashTags in the tweet.
   * @param hts list of HashTags in the tweet.
   */
  public void setHashTags(List<EntityHashTag> hts){
    HashTags = hts;
  }
  
  /**
   * Sets the list of UserRefs in the tweet.
   * @param urs the list of UserRefs in the tweet.
   */
  public void setUserRefs(List<EntityUserRef> urs){
    URefs = urs;
  }
  
  /**
   * Sets the list of URLS in the tweet.
   * @param urls the list of URLS in the tweet.
   */
  public void setURLs(List<EntityURL> urls){
    URLs = urls;
  }
  
  /**
   * Sets the list of eMedias in the tweet.
   * @param medias the list of eMedias in the tweet.
   */
  public void setMedias(List<EntityMedia> medias){
    EMedias = medias;
  }
  
  /**
   * Sets the sentiment categories and data of the tweet.
   * @param prop Parameters from the _setup.txt file.
   */
  public void setSentiment(PropertiesTD prop){
    List<String> arr = new ArrayList<>();
    for (Language lang : Language.values()) {
      arr.add(lang.name());
    }
    
    if(sentLang == null){
      sentLang = new SentiStrengthLang(prop);
    }
    if(!arr.contains(getLanguage().toUpperCase())){
      ss = null;
    }
    else{
      try{
        String[] sToken = sentLang.detect(Language.valueOf(getLanguage().toUpperCase()), getText());
        byte ss1 = Byte.valueOf(sToken[1]);
        byte ss2 = Byte.valueOf(sToken[2]);
        byte ss3 = Byte.valueOf(sToken[3]);
        byte ss4 = Byte.valueOf(sToken[4]);
        ss = new TSenti(sToken[0], ss1, ss2, ss3, ss4);
      }
      catch(Exception e){
        System.err.println(e);
      }
    }
  }
  
  /**
   * Sets true if the tweet contains if any keyword listed in the parameters.
   * @param prop Parameters from the _setup.txt file.
   */
  public void setHasKeyword(PropertiesTD prop){
    has_keyword = false;
    for (String kw : prop.keywords) {
      if(this.getText().toLowerCase().contains(kw)){
        has_keyword = true;
        break;
      }
    }
  }
  
  /**
   * Sets the user of its blacklisted or not.
   * @param bl true to blacklist the user.
   */
  public void setBlacklist(boolean bl){
    blacklisted = bl;
  }

  //////////////////////    GETTERS     //////////////////////
  /**
   * Returns the Download Date in format "YYYYMMDD HH:mm:ss Z".
   * @return the Download Date.
   */
  public Date   getDownloadedAt(){
    return downloaded_at;
  }
  
  /**
   * Returns the Creation Date in format "YYYYMMDD HH:mm:ss Z".
   * @return the Creation Date.
   */
  public Date   getCreatedAt(){
    return created_at;
  }
  
  /**
   * Returns the Tweet ID.
   * @return the Tweet ID.
   */
  public long   getId(){
    return id;
  }
  
  /**
   * Returns the type of the Tweet (TWEET, QUOTED, RT_ROOT).
   * @return the type of the Tweet.
   */
  public TweetType   getType(){
    return type;
  }
  
  /**
   * Returns the Text corpus of the tweet.
   * @return the Text corpus of the tweet.
   */
  public String getText(){
    return text;
  }
  
  /**
   * Returns the language of the text of the tweet (ISO 2 Language Code - LowerCase).
   * @return the language of the text of the tweet (ISO 2 Language Code - LowerCase).
   */
  public String getLanguage() {
    return language;
  }
  
  /**
   * Returns how many times this tweets was retweeted.
   * @return how many times this tweets was retweeted.
   */
  public long getRTCount(){
    return retweet_count;
  }
  
  /**
   * Returns the Tweet ID of the RT_Root.
   * @return the Tweet ID of the RT_Root.
   */
  public long getIdRT(){
    return id_rt;
  }
  
  /**
   * Returns the original text of the ReTweet.
   * @return the original text of the ReTweet. 
   */
  public String getRTText(){
    return text_rt;
  }
  
  /**
   * Returns the Tweet ID of the Quoted_Root tweet.
   * @return the Tweet ID of the Quoted_Root tweet.
   */
  public long getIdQuoted(){
    return id_quoted;
  }  
  
  /**
   * Returns the original text of the Quoted Tweet.
   * @return the original text of the Quoted Tweet. 
   */
  public String getTextQuoted(){
    return text_quoted;
  }
  
  /**
   * Returns the source device or site where the tweet was posted.
   * @return the source device or site where the tweet was posted.
   */
  public String getSource(){
    return source;
  }
  
  /**
   * Returns the HREF source where the tweet was posted.
   * @return the HREF source where the tweet was posted.
   */
  public String getSrcHref(){
    return src_href;
  }
  
  /**
   * Returns the source_rel of the tweet.
   * @return the source_rel of the tweet.
   */
  public String getSrcRel(){
    return src_rel;
  }
  
  /**
   * Returns the source device.
   * @return the source device.
   */
  public String getSrcText(){
    return src_text;
  }
  
  /**
   * Returns the list of countries detected in text.
   * @return the list of countries detected in text.
   */
  public Set<String> getCountriesText(){
    return countries_text;
  }
  
  /**
   * Returns the User ID if the tweet is a reply.
   * @return the User ID if the tweet is a reply.
   */
  public long getIn_reply_to_user_id(){
    return in_reply_to_user_id;
  }
  
  /**
   * Returns the Tweet ID of the replied tweet.
   * @return the Tweet ID of the replied tweet.
   */
  public long getIn_reply_to_status_id(){
    return in_reply_to_status_id;
  }
  
  /**
   * Returns the User Screen_Name(@) of the user that replied the tweet.
   * @return the User Screen_Name(@) of the user that replied the tweet.
   */
  public String getIn_reply_to_screen_name(){
    return in_reply_to_screen_name;
  }
  
  /**
   * Returns the GPS Latitude of the Geo-Location of the tweet.
   * @return the GPS Latitude of the Geo-Location of the tweet.
   */
  public double getGeoLatitude(){
    return geo_latitude;
  }
  
  /**
   * Returns the GPS Longitude of the Geo-Location of the tweet.
   * @return the GPS Longitude of the Geo-Location of the tweet.
   */
  public double getGeoLongitude(){
    return geo_longitude;
  }
  
  /**
   * Returns the USER of the tweet.
   * @return the USER of the tweet.
   */
  public TUser  getUser(){
    return tuser;
  }
  
  /**
   * Returns the PLACE of the geo-location of the tweet.
   * @return the PLACE of the geo-location of the tweet.
   */
  public TPlace getPlace(){
    return place;
  }
  
  /**
   * Returns the list of HashTags in the tweet.
   * @return the list of HashTags in the tweet.
   */
  public List<EntityHashTag> getHashTags(){
    return HashTags;
  }
  
  /**
   * Returns the list of UserRefs in the tweet.
   * @return the list of UserRefs in the tweet.
   */
  public List<EntityUserRef> getUserRefs(){
    return URefs;
  }
  
  /**
   * Returns the list of URLS in the tweet.
   * @return the list of URLS in the tweet.
   */
  public List<EntityURL> getURLs(){
    return URLs;
  }
  
  /**
   * Returns the list of eMedias in the tweet.
   * @return the list of eMedias  in the tweet.
   */
  public List<EntityMedia> getMedias(){
    return EMedias;
  }
  
  /**
   * Tests if it is a RT.
   * @return true if it is a RT.
   */
  public boolean isRetweet(){
    return retweet;
  }
  
  /**
   * Tests if the User posted in the lasts minutes.
   * @return true the User posted another tweet in the las minutes.
   */
  public boolean isRepeatedUser(){
    return repeated_user;
  }
  
  /**
   * Tests if it is a Quoted Tweet.
   * @return true if it is a Quoted Tweet.
   */
  public boolean isQuote(){
    return isQuote;
  }
  
  /**
   * Tests if the text was truncated.
   * @return true if the text was truncated.
   */
  public boolean isTruncated(){
    return truncated;
  }
  
  /**
   * Returns true if the text contains any of the keyword list from the _setup.txt.
   * @return true if the text contains any of the keyword list.
   */
  public boolean hasKeyword(){
    return has_keyword;
  }
  
  /**
   * Tests if it is a favorited.
   * @return true if it is a favorited.
   */
  public boolean isFavorited(){
    return favorited;
  }
  
  /**
   * Tests if it has geolocation.
   * @return true if it has geolocation.
   */
  public boolean isGeolocated(){
    return geolocated;
  }
  
  /**
   * Returns the Download date in string format "YYYYMMDD HH:mm:ss Z"
   * @return the Download date in string format "YYYYMMDD HH:mm:ss Z"
   */
  public String getDownloadedAt_String(){
    return TUtils.Date_Formatter(downloaded_at);
  }
  
  /**
   * Returns the Created date in string format "YYYYMMDD HH:mm:ss Z"
   * @return the Created date in string format "YYYYMMDD HH:mm:ss Z"
   */
  public String getCreatedAt_String(){
    return TUtils.Date_Formatter(created_at);
  }
  
  /**
   * Returns the sentiment information of the tweet. Null if it has not.
   * @return the sentiment information of the tweet.
   */
  public TSenti getSentiment(){
    return ss;
  }
  
  /**
   * Test if the user is blacklisted.
   * @return true if the user is blacklisted.
   */
  public boolean isBlacklisted(){
    return blacklisted;
  }
  
  /**
   * Returns a string representation of the Tweet class.
   * @return Returns the string representation of the classs.
   */
  @Override
  public String toString(){
    String sgeo = "false\tnull\tnull";
    if(geolocated){
      sgeo = "true\t" + String.format("%8f", geo_latitude) 
               + "\t" + String.format("%8f", geo_longitude);
    }

    String str_q = "false\tnull\tnull";
    if(isQuote()){
      str_q = Boolean.toString(isQuote()) +"\t"+
              Long.toString(getIdQuoted()) +"\t"+ 
              getTextQuoted();
    }

    String str_rt = "false\t"+this.getRTCount()+"\tnull\tnull";
    if(isRetweet()){
      str_rt = isRetweet() +"\t"+ 
               getRTCount() +"\t"+ 
               getIdRT() +"\t"+ 
               getRTText();
    }
    
    String str_place = "null\tnull\tnull\tnull\tnull\tnull\tnull\tnull";
    if(place != null){
      str_place = place.getId()         +"\t"+
                  place.getName()       +"\t"+
                  place.getCountryCode() +"\t"+
                  place.getCountry()    +"\t"+
                  place.getFullName()   +"\t"+
                  place.getStreetAddress() +"\t"+
                  place.getUrl()           +"\t"+
                  place.getPlaceType();
    }
    String str_ss = "null\tnull\tnull\tnull\tnull";
    if(ss != null){
      str_ss =  ss.getLang()+"\t"+
                ss.getNegative()+"\t"+
                ss.getPositive()+"\t"+
                ss.getNeutral()+"\t"+
                ss.getPolarity();
    }
    
    String str =  this.getDownloadedAt_String()        +"\t"+
                  this.getCreatedAt_String()           +"\t"+
                  Long.toString(this.getId())          +"\t"+
                  this.isBlacklisted()                 +"\t"+
                  getUser().getId()               +"\t"+
                  repeated_user                   +"\t"+
                  Boolean.toString(isFavorited()) +"\t"+
                  Boolean.toString(isTruncated()) +"\t"+
                  type                            +"\t"+
                  getLanguage()                   +"\t"+
                  getText()                       +"\t"+
                  str_rt                          +"\t"+
                  str_q                           +"\t"+
                  this.hasKeyword()               +"\t"+
                  getSrcHref()                    +"\t"+
                  getSrcRel()                     +"\t"+
                  getSrcText()                    +"\t"+
                  getIn_reply_to_status_id()      +"\t"+
                  getIn_reply_to_user_id()        +"\t"+
                  getIn_reply_to_screen_name()    +"\t"+
                  this.getCountriesText()         +"\t"+
                  sgeo                            +"\t"+
                  str_place                       +"\t"+
                  str_ss                          +"\t"+      //SENTIMENT
                  getUser().toString()            +"\t"+
                  getHashTags().toString()        +"\t"+
                  getUserRefs().toString()        +"\t"+
                  getURLs().toString()            +"\t"+
                  getMedias().toString();
                 
    return str;
  }
}

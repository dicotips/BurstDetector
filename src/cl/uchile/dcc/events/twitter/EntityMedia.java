package cl.uchile.dcc.events.twitter;

import java.util.Map;
import twitter4j.MediaEntity.Size;

/**
* This Class represents an Entity of Medias inside of a tweet. This entry will 
* be inserted into the database.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public final class EntityMedia{
  /** ID of the media */
  private long id;
  
  /** Short and long URLs to locate the media object. */
  private String media_url, media_url_https;
  
  /** Type of the media object. */
  private String media_type;
  
  /** Meta-Data of the media. It specifies the dimensions in pictures. */
  private String sizes;

  /**
   * Constructor of an EntityMedia.
   * @param id        ID of the media.
   * @param m_url     HTTP URL to locate the media object.
   * @param m_url_s   HTTPS URL to locate the media object.
   * @param m_type    Type of the media object.
   * @param m_sizes   META-DATA of the media object.
   */
  public EntityMedia(long id, String m_url, String m_url_s, String m_type, Map<Integer, Size> m_sizes){
    setId(id);
    setMediaURL(m_url);
    setMediaURLHttps(m_url_s);
    setType(m_type);
    setSizes(m_sizes);
  }
  
  /**
   * Sets the ID of the media Object.
   * @param vid ID of the media Object.
   */
  public void setId(long vid){
    id = vid;
  }
  
  /**
   * Sets the HTTP URL to locate the media object.
   * @param vurl HTTP URL to locate the media object.
   */
  public void setMediaURL(String vurl){
    media_url = vurl;
  }
  
  /**
   * Sets the HTTPS URL to locate the media object.
   * @param m_url_s HTTPS URL to locate the media object.
   */
  public void setMediaURLHttps(String m_url_s){
    media_url_https = m_url_s;
  }
  
  /**
   * Sets the Type of the media object.
   * @param m_type Type of the media object.
   */
  public void setType(String m_type){
    media_type = m_type;
  }
  
  /** 
   * Sets the META-DATA of the media object. 
   * @param m_sizes META-DATA of the media object. 
   */
  public void setSizes(Map<Integer, Size> m_sizes){
    sizes = m_sizes.toString();
  }
  
  /** 
   * Sets the META-DATA of the media object as an String. 
   * @param m_sizes META-DATA of the media object. 
   */
  public void setSizes(String m_sizes){
    sizes = m_sizes;
  }

  /**
   * Gets the ID of the media Object.
   * @return Returns the ID of the media Object.
   */
  public long getId(){
    return id;
  }
  
  /**
   * Gets the HTTP URL to locate the media object.
   * @return Returns the HTTP URL to locate the media object.
   */
  public String getMediaURL(){
    return media_url;
  }
  
  /**
   * Gets the HTTPS URL to locate the media object.
   * @return Returns the HTTPS Returns the URL to locate the media object.
   */
  public String getMediaURLHttps(){
    return media_url_https;
  }
  
  /**
   * Gets the Type of the media object.
   * @return Returns the Type of the media object.
   */
  public String getType(){
    return media_type;
  }
  
  /** 
   * Gets the META-DATA of the media object. 
   * @return Returns the META-DATA of the media object. 
   */
  public String getSizes(){
    return sizes;
  }

  /**
   * Returns a string representation of the EntityHashTag class.
   * Format: "id  media_url  media_url_https  media_type sizes"
   * 
   * @return Returns the string representation of the classs.
   */
  @Override
  public String toString(){
    String str =  "{"+
                  getId()             +", "+ 
                  getMediaURL()       +", "+
                  getMediaURLHttps()  +", "+
                  getType()           +", "+
                  getSizes()
                  +"}";
    return str;
  }
}

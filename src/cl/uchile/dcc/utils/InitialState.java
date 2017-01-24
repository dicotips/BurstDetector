package cl.uchile.dcc.utils;

/**
* This Enum Class represents the State of the initial of the process.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-09-30
*/
public enum InitialState {
  NEW_STREAM,
  NEW_DATABASE, 
  RECOVERY_STREAM,
  RECOVERY_DATABASE;
}
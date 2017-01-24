package cl.uchile.dcc.ml;

import cl.uchile.dcc.utils.PropertiesTD;
import cl.uchile.dcc.utils.TUtilsDescriber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* This Class test the use of Frequent Itemsets Algorithms.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public class TestBursty {
  
  /**
   * This is the main method tests the use of the The Apriori Algorithm using
   * text mesages in diferent languages.
   * 
   * @param  args Nothing
   */
  public static void main(String[] args) {
    
    PropertiesTD prop = new PropertiesTD("/Users/dicotips/Dropbox/Research_SourceCode/Twitter_Crawler/_setup.txt");
    
    List<Set<String>> itemsetList = new ArrayList<>();
    
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","00","揖斐川","#熊本地震","05","#速報","西目屋","赤井川","#地震","ちばけんま","18、北海道","64、鳥取県","青森県","関金","5、岐阜県")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","00","#地震","05","鳥取県","関金")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","00","揖斐川","赤井川","66","#地震","05","#ゆじの地震","21、北海道","岐阜県")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","00","揖斐川","赤井川","#地震","05","81","勝浦北","23、北海道","6、岐阜県","#ゆじの地震","千葉県")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","00","揖斐川","#熊本地震","05","#速報","勝浦北","6、岐阜県","21、北海道","千葉県","赤井川","#地震","ちばけんま","76")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","00","赤井川","#地震","05","北海道","#ゆじの地震","74")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","00","赤井川","#地震","05","北海道","81")));
    itemsetList.add(new HashSet<>(Arrays.asList("00","05","大阪","波形","生坂","関金、大阪府","赤井川","#地震","西目屋、鳥取県","標茶南、岐阜県","長野県","地中","谷汲、千葉県","勝浦東、青森県","御代田、北海道")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","赤井川","01","#地震","05","北海道","95","#ゆじの地震")));
    itemsetList.add(new HashSet<>(Arrays.asList("揖斐川","01","標茶南","05","大阪","3、大阪府","勝浦北","#ゆじの地震","生坂","西目屋","赤井川","2、岐阜県","#地震","6、青森県","長野県","207","23、千葉県","165、北海道","5、鳥取県","関金","御代田","98")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","198、北海道","赤井川","01","89","#地震","ちばけんま","#熊本地震","05","長野県","#速報","生坂")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","赤井川","01","#地震","05","北海道","90","#ゆじの地震")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","#地震","02","標茶南","05","北海道","#ゆじの地震")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","#地震","ちばけんま","02","標茶南","#熊本地震","05","北海道","#速報")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","揖斐川","#地震","03","25","05","#ゆじの地震","岐阜県")));
    itemsetList.add(new HashSet<>(Arrays.asList("地震情報】","#地震","03","05")));
    itemsetList.add(new HashSet<>(Arrays.asList("11","wnw","05","52km","43…","19","earthquake","point","utc2016","alaska","itime2016","anchor","dyfi","43")));
    itemsetList.add(new HashSet<>(Arrays.asList("33","34","hatillo","14","04","05","00…","18","earthquake","puerto","utc2016","rico","itime2016","dyfi","58km")));
    itemsetList.add(new HashSet<>(Arrays.asList("マグニチュード：1","04","05","#jishin","06","地震】発生日時：2016","深さ：8","5572度","東経130","#地震","地震情報検索","#jisin","6km","net版","震源地：北緯32","7439度","42")));
    itemsetList.add(new HashSet<>(Arrays.asList("44","マグニチュード：0","04","05","#jishin","06","地震】発生日時：2016","深さ：8","1234度","#地震","地震情報検索","2km","東経131","#jisin","net版","震源地：北緯32","9939度")));
    itemsetList.add(new HashSet<>(Arrays.asList("マグニチュード：0","47","04","05","#jishin","06","地震】発生日時：2016","1123度","深さ：7","#地震","地震情報検索","東経131","#jisin","震源地：北緯33","net版","0028度","9km")));
    itemsetList.add(new HashSet<>(Arrays.asList("マグニチュード：0","04","48","05","#jishin","06","地震】発生日時：2016","5760度","深さ：7","東経130","#地震","地震情報検索","3km","#jisin","7019度","net版","震源地：北緯32")));
    itemsetList.add(new HashSet<>(Arrays.asList("マグニチュード：0","04","05","7952度","#jishin","06","地震】発生日時：2016","7740度","深さ：7","東経130","#地震","地震情報検索","#jisin","net版","震源地：北緯32","7km","50")));
    itemsetList.add(new HashSet<>(Arrays.asList("マグニチュード：1","04","深さ：13","05","#jishin","06","8897度","地震】発生日時：2016","8814度","東経130","#地震","地震情報検索","#jisin","net版","震源地：北緯32","7km","51")));
    itemsetList.add(new HashSet<>(Arrays.asList("マグニチュード：1","1570度","震源地：北緯38","04","05","#jishin","06","地震】発生日時：2016","深さ：31","#地震","地震情報検索","東経142","#jisin","net版","8km","2932度","52")));
    itemsetList.add(new HashSet<>(Arrays.asList("マグニチュード：1","04","2517度","05","#jishin","06","地震】発生日時：2016","3677度","深さ：9","#地震","地震情報検索","東経131","#jisin","5km","震源地：北緯33","net版","53")));
    itemsetList.add(new HashSet<>(Arrays.asList("55","マグニチュード：0","04","05","#jishin","06","7622度","地震】発生日時：2016","深さ：8","東経130","#地震","地震情報検索","1km","7112度","#jisin","net版","震源地：北緯32")));
    itemsetList.add(new HashSet<>(Arrays.asList("マグニチュード：0","58","04","05","#jishin","06","地震】発生日時：2016","1786度","#地震","地震情報検索","9678度","深さ：1","3km","東経131","#jisin","net版","震源地：北緯32")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","揖斐川","#地震","04","26","05","#ゆじの地震","岐阜県")));
    itemsetList.add(new HashSet<>(Arrays.asList("03","05","大阪","波形","生坂","関金、大阪府","赤井川","#地震","西目屋、鳥取県","標茶南、岐阜県","長野県","地中","谷汲、千葉県","勝浦東、青森県","御代田、北海道")));
    itemsetList.add(new HashSet<>(Arrays.asList("揖斐川","04","標茶南","05","4、鳥取県","大阪","勝浦北","生坂","西目屋","赤井川","25、千葉県","2、岐阜県","#地震","6、青森県","長野県","208","93","関金","御代田","176、北海道","2、大阪府")));
    itemsetList.add(new HashSet<>(Arrays.asList("04","05","長野県","地震速報】","#地震　#earthquake","御代田")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","揖斐川","#地震","地震】","25","04","05","岐阜県")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","揖斐川","23","#地震","ちばけんま","04","#熊本地震","05","#速報","岐阜県")));
    itemsetList.add(new HashSet<>(Arrays.asList("第1報","計測震度：0","04","05","49","06","6gal","茨城県","#地震","地震検知都道府県数：1","地震検知2016","時刻：05","43")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","166","#地震","05","長野県","#ゆじの地震","御代田")));
    itemsetList.add(new HashSet<>(Arrays.asList("強震モニタ監視","茨城県","#地震","第1報　検出：05","推定震度：0未満","04","1都道府県で検出","05","#jishin","6gal","48現在","43")));
    itemsetList.add(new HashSet<>(Arrays.asList("66","33","2016","05","38","127","29","08","108","earthquake","halmahera","indonesia")));
    itemsetList.add(new HashSet<>(Arrays.asList("5region  crete","#sismo","greece","05","csem","atencion","ems","magnitude  m","greecedate","time  2016","crete")));
    itemsetList.add(new HashSet<>(Arrays.asList("11","southern","magnitude  ml","#sismo","#cs","05","alaska","19","time  2016","alaskadate","0region  southern","43")));
    itemsetList.add(new HashSet<>(Arrays.asList("regiondate","0region  puerto","puerto","#sismo","#cs","05","rico","md","18","magnitude  md","time  2016","region")));
    itemsetList.add(new HashSet<>(Arrays.asList("5region  crete","56","45","#sismo","05","19","time  2016","crete","#cs","greece","magnitude  m","greecedate","ut")));
    itemsetList.add(new HashSet<>(Arrays.asList("southern","magnitude  ml","#sismo","05","alaska","#csismica","19","time  2016","alaskadate","0region  southern")));
    itemsetList.add(new HashSet<>(Arrays.asList("regiondate","0region  puerto","puerto","#sismo","05","rico","md","#csismica","magnitude  md","time  2016","region")));
    itemsetList.add(new HashSet<>(Arrays.asList("5region  crete","56","#sismo","greece","05","#csismica","19","magnitude  m","greecedate","time  2016","crete")));
    itemsetList.add(new HashSet<>(Arrays.asList("#csem","56","#sismo","#greece","2016","05","utc","#temblor","#emsc","19","crete")));
    itemsetList.add(new HashSet<>(Arrays.asList("揖斐川","1、岐阜県","標茶南","05","5、青森県","07","大阪","3、大阪府","勝浦北","#ゆじの地震","生坂","24、千葉県","西目屋","赤井川","#地震","167、北海道","202","3、鳥取県","長野県","82","関金","御代田")));
    itemsetList.add(new HashSet<>(Arrays.asList("23","#地震","05","06","08","地中カウント累積マップ")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","#地震","05","青森県","08","#ゆじの地震","西目屋")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","#地震","標茶南","05","北海道","09","#ゆじの地震")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","#地震","地震】","標茶南","05","北海道","09")));
    itemsetList.add(new HashSet<>(Arrays.asList("05","49","arizona","m3","nw","earthquake","eq","april","@azgeology","mst","miles","2016","swarm","32")));
    itemsetList.add(new HashSet<>(Arrays.asList("地中カウント増加","#地震","05","青森県","09","#ゆじの地震","西目屋")));
    itemsetList.add(new HashSet<>(Arrays.asList("揖斐川","78","166、北海道","標茶南","05","25、青森県","4、鳥取県","大阪","09","勝浦北","生坂","西目屋","赤井川","2、岐阜県","#地震","長野県","206","5、千葉県","関金","御代田","2、大阪府")));
    itemsetList.add(new HashSet<>(Arrays.asList("manabi","fund","@generosity","raise","emergency","earthquake","funds","relief","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("manabi","fund","@generosity","raise","emergency","earthquake","funds","relief","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("manabi","fund","@generosity","raise","emergency","earthquake","funds","relief","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("manabi","fund","@generosity","raise","emergency","earthquake","funds","relief","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("manabi","fund","@generosity","raise","emergency","earthquake","funds","relief","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("manabi","fund","@generosity","raise","emergency","earthquake","funds","relief","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("manabi","fund","@generosity","raise","emergency","earthquake","funds","relief","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("manabi","fund","@generosity","raise","emergency","earthquake","funds","relief","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("manabi","fund","@generosity","raise","emergency","earthquake","funds","relief","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("manabi","fund","@generosity","raise","emergency","earthquake","funds","relief","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("manabi","fund","@generosity","raise","emergency","earthquake","funds","relief","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("manabi","fund","@generosity","raise","emergency","earthquake","funds","relief","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("manabi","fund","@generosity","raise","emergency","earthquake","funds","relief","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("manabi","recaudan","dinero","unico","@2010compudavid","deben","terremoto","destino","co…","prestamos","fondos","gasto")));
    itemsetList.add(new HashSet<>(Arrays.asList("offers","#video","living","young","#yleo","earthquake","relief","ecuador","disaster")));
    itemsetList.add(new HashSet<>(Arrays.asList("provide","ravaged","voted","emergency","earthquake","join","family","#kutoa","ecuador","kits")));
    itemsetList.add(new HashSet<>(Arrays.asList("independiente","donar","damnificados","image","volvera","taquilla","terremoto","presiden","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("independiente","donar","damnificados","image","volvera","taquilla","terremoto","presiden","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("independiente","donar","damnificados","image","volvera","taquilla","#ecuador","terremoto","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("independiente","donar","damnificados","image","volvera","taquilla","terremoto","presiden","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("conoce","asiste","damnificados","duran","ciudad","@alcaldiaduran","terremoto","resiliente")));
    itemsetList.add(new HashSet<>(Arrays.asList("@ecuainm","mil","convocados","damnificados","voluntarios","han","sido","alrededor","17","terremoto","ayudar")));
    itemsetList.add(new HashSet<>(Arrays.asList("donar","damnificados","volvera","taquilla","@idv","@andesecuador","terremoto►","ec")));
    itemsetList.add(new HashSet<>(Arrays.asList("@ecuainm","mil","convocados","damnificados","voluntarios","han","sido","alrededor","17","terremoto","ayudar")));
    itemsetList.add(new HashSet<>(Arrays.asList("independiente","donar","damnificados","volvera","taquilla","andesecua…","terremoto","ecuador","andes","info")));
    itemsetList.add(new HashSet<>(Arrays.asList("independiente","donar","damnificados","volvera","taquilla","terremoto","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("donar","damnificados","volvera","taquilla","@idv","@andesecuador","terremoto►","ec")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("damnificados","abril","ministro","ternicier","ayuda","@minagricl","terremoto","participa","avion","carga","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("damnificados","#miami","proxima","semana","#ecuador","recaudar","#carlosvives","detalles","terremoto","fondos","cantara")));
    itemsetList.add(new HashSet<>(Arrays.asList("000","recauda","damnificados","china","usd","#terremoto","250","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("@ecuavisainforma","000","recauda","damnificados","china","usd","#terremoto","250","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("@correistas","independiente","donar","damnificados","image","volvera","taquilla","terremoto","presiden","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("@ecuavisainforma","000","recauda","damnificados","china","usd","#terremoto","250","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("#barcelonasc","damnificados","donara","cifra","terremoto")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("@fotocolectora","donacion","difusion","damnificados","@retratosecuador","@cultura","coordinaron","@embajadaecuesp","ec","htt…")));
    itemsetList.add(new HashSet<>(Arrays.asList("donar","damnificados","volvera","taquilla","@idv","@andesecuador","terremoto►","ec")));
    itemsetList.add(new HashSet<>(Arrays.asList("@ecuavisainforma","000","recauda","damnificados","china","usd","#terremoto","250","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("@ecuavisainforma","000","recauda","damnificados","china","usd","#terremoto","250","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("@ecuavisainforma","000","recauda","damnificados","china","usd","#terremoto","250","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("conoce","asiste","damnificados","duran","ciudad","@alcaldiaduran","terremoto","resiliente")));
    itemsetList.add(new HashSet<>(Arrays.asList("independiente","donar","damnificados","volvera","taquilla","valle","terremoto")));
    itemsetList.add(new HashSet<>(Arrays.asList("@onemichile","damnificados","16","ayuda","@fach","@g","llegando","humanitaria","manta","terremoto","ascencio","chile")));
    itemsetList.add(new HashSet<>(Arrays.asList("solidaridad","damnificados","acopio","mantiene","centros","terremoto","#yoprefierolaverdad","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("damnificados","barcelona","donara","cifra","terremoto","#yoprefierolaverdad")));
    itemsetList.add(new HashSet<>(Arrays.asList("阿蘇山北東＋北西、有明海も地震増加で注意","熊本地震の余震活動が徐々に周辺域に拡散…　熊本市南側","#地震","#jishin","@hatanaka810","の断層が徐々に崩壊しており、日奈久断層帯","https…","日奈久区間","の部分的な崩壊が続いており厳重警戒","宇城市－八代市")));
    itemsetList.add(new HashSet<>(Arrays.asList("阿蘇山北東＋北西、有明海も地震増加で注意","熊本地震の余震活動が徐々に周辺域に拡散…　熊本市南側","#地震","#jishin","@hatanaka810","の断層が徐々に崩壊しており、日奈久断層帯","https…","日奈久区間","の部分的な崩壊が続いており厳重警戒","宇城市－八代市")));
    itemsetList.add(new HashSet<>(Arrays.asList("#jishin","@tenkijp","jishin","6日0時52分頃、熊本県で最大震度2を観測する地震がありました。震源地は熊本県熊本地方、m2","7。この地震による津波の心配はありません。")));
    itemsetList.add(new HashSet<>(Arrays.asList("#jishin","@tenkijp","jishin","6日1時8分頃、熊本県で最大震度1を観測する地震がありました。震源地は熊本県熊本地方、m2","2。この地震による津波の心配はありません。")));
    itemsetList.add(new HashSet<>(Arrays.asList("#jishin","@tenkijp","jishin","6日2時3分頃、熊本県で最大震度2を観測する地震がありました。震源地は熊本県熊本地方、m3","1。この地震による津波の心配はありません。")));
    itemsetList.add(new HashSet<>(Arrays.asList("6日3時15分頃、熊本県で最大震度1を観測する地震がありました。震源地は熊本県阿蘇地方、m2","#jishin","@tenkijp","jishin","1。この地震による津波の心配はありません。")));
    itemsetList.add(new HashSet<>(Arrays.asList("despues","@antovallina","gila","callate","bueno","dormir","decime")));
    itemsetList.add(new HashSet<>(Arrays.asList("hablen","despues","#manabi","quiero","terremoto","ecuador","@jorgeglas")));
    itemsetList.add(new HashSet<>(Arrays.asList("@informacioncabj","vale","@tato","despues","@edls1899","aguilera","@estudiofutbol","siguen","vola","romper","orto","provinciano")));
    itemsetList.add(new HashSet<>(Arrays.asList("hablen","despues","#manabi","@vice","quiero","terremoto","ec","ecuador","@jorgeglas")));
    itemsetList.add(new HashSet<>(Arrays.asList("hablen","despues","#manabi","@vice","quiero","terremoto","ec","ecuador","@jorgeglas")));
    itemsetList.add(new HashSet<>(Arrays.asList("hablen","despues","#manabi","@vice","quiero","terremoto","ec","ecuador","@jorgeglas")));
    itemsetList.add(new HashSet<>(Arrays.asList("hablen","despues","#manabi","@vice","quiero","terremoto","ec","ecuador","@jorgeglas")));
    itemsetList.add(new HashSet<>(Arrays.asList("hablen","despues","#manabi","@vice","quiero","terremoto","ec","ecuador","@jorgeglas")));
    itemsetList.add(new HashSet<>(Arrays.asList("hablen","despues","#manabi","@vice","quiero","terremoto","ec","ecuador","@jorgeglas")));
    itemsetList.add(new HashSet<>(Arrays.asList("hablen","despues","#manabi","@vice","quiero","terremoto","ec","ecuador","@jorgeglas")));
    itemsetList.add(new HashSet<>(Arrays.asList("calbuco","pizarro","cochamo","anda","pto","diputado","vallespin","sudafrica","despues","maullin","montt","inglatera","terremoto","coquimbo","dc")));
    itemsetList.add(new HashSet<>(Arrays.asList("hablen","despues","#manabi","@vice","quiero","terremoto","ec","ecuador","@jorgeglas")));
    itemsetList.add(new HashSet<>(Arrays.asList("hablen","despues","#manabi","@vice","quiero","terremoto","ec","ecuador","@jorgeglas")));
    itemsetList.add(new HashSet<>(Arrays.asList("hablen","despues","#manabi","@vice","quiero","terremoto","ec","ecuador","@jorgeglas")));
    itemsetList.add(new HashSet<>(Arrays.asList("hablen","despues","#manabi","@vice","quiero","terremoto","ec","ecuador","@jorgeglas")));
    itemsetList.add(new HashSet<>(Arrays.asList("frio","despues","gorda😪","comer","quejo","impulsa")));
    itemsetList.add(new HashSet<>(Arrays.asList("despues","elegir","terremoto","morir","sobrevivir","gripa")));
    itemsetList.add(new HashSet<>(Arrays.asList("00","34","25","04","16","49","08","earthquake","gmt","761","uk","303","location","magnitude","lat","21")));
    itemsetList.add(new HashSet<>(Arrays.asList("proteger","#uges","comparte","caso","comenta","hijos","sismo","compartelo","debes")));
    itemsetList.add(new HashSet<>(Arrays.asList("ahora","seguro","importante","prestar","@catazanardo","caso","capitulo","atencion")));
    itemsetList.add(new HashSet<>(Arrays.asList("fiate","vistazo","caso","recuerda","oficiales","echa","#terremoto","consejos","utiles","@policia","fuentes")));
    itemsetList.add(new HashSet<>(Arrays.asList("debemos","informacion","caso","tienes","enlace","terremoto","@ignspain")));
    itemsetList.add(new HashSet<>(Arrays.asList("caso","recu…","ong","terremoto","iae")));
    itemsetList.add(new HashSet<>(Arrays.asList("motivaste","poquito","lucerov","tarde","gran","moneda","@comunidad","traslado","escuche","ivaibaby","@karol","adoro")));
    itemsetList.add(new HashSet<>(Arrays.asList("activar","tarde","vamo")));
    itemsetList.add(new HashSet<>(Arrays.asList("faltado","llegado","amo","tarde","colegio","veces","faltas","banda","10")));
    itemsetList.add(new HashSet<>(Arrays.asList("libro","escuchando","clima","middle","tarde","leyendo","perfecto","hermosa")));
    itemsetList.add(new HashSet<>(Arrays.asList("@jara","@lidiacarvajalve","mo","tarde","@yessicaguerrac1","besitos","amiga","@hmcfstilorock","buena")));
    itemsetList.add(new HashSet<>(Arrays.asList("@nicolasmaduro","@ntn24","#miraflores","pronto","tarde","caera","temprano","#senores","regimen","#dictadurasanguinaria","seguros")));
    itemsetList.add(new HashSet<>(Arrays.asList("venta","compras","los…","durante","atencion","kkaracueros","especial","hagan","dia","madre")));
    itemsetList.add(new HashSet<>(Arrays.asList("pa´pasas","diamond","ch","confunda","dame","pasa","chaucha","madre","@mike")));
    itemsetList.add(new HashSet<>(Arrays.asList("sola","#tarjetaspersonalizadas","#happymothersday…","tarjetitas","madre")));
    itemsetList.add(new HashSet<>(Arrays.asList("florida","#like4like","#instachile","valpo","cerro","valparaiso")));
    itemsetList.add(new HashSet<>(Arrays.asList("100","recibieron","@biobio","delitos","condicional","sexuales","libertad","condenados","valparaiso")));
    itemsetList.add(new HashSet<>(Arrays.asList("vina","clases","vivaldi","edificio","haciendo","mar","valparaiso")));
    itemsetList.add(new HashSet<>(Arrays.asList("uv","sociales","derecho","facultad","ciencias","@uvalpochile","valparaiso")));
    itemsetList.add(new HashSet<>(Arrays.asList("ver","falta","boca")));
    itemsetList.add(new HashSet<>(Arrays.asList("@luanagoenaga1","apretar","poquito","falta","sos","freno","kpa")));
    itemsetList.add(new HashSet<>(Arrays.asList("tipo","han","hecho","falta","clases","aclararle","cuenta","@juanarcaya","civica","damos","educacion","cosas")));
    itemsetList.add(new HashSet<>(Arrays.asList("1985","bolivia","falta","se…","rurales","areas","recursos","principalmente")));
    itemsetList.add(new HashSet<>(Arrays.asList("pensando","falta","comi","grego😍","dale","vez","milanesas")));
    itemsetList.add(new HashSet<>(Arrays.asList("satisfactoria","falta","telefonica","explicacion","@vtrchile","instalen","cable")));
    itemsetList.add(new HashSet<>(Arrays.asList("rinon","preocupa","alma","falta","interesados","serian","bueno","@kakysf","hablo","momento")));
    itemsetList.add(new HashSet<>(Arrays.asList("falta","trabajos","todavia")));
    itemsetList.add(new HashSet<>(Arrays.asList("dar","financiamos","falta","contribuyentes","aparato","ciudadanos","publico","chilenos")));
    itemsetList.add(new HashSet<>(Arrays.asList("arroz","huevo","duro","@novendemoschile","frutas","reciben","sucediendo","colegios","ensaladas","pasa","colegio")));
    itemsetList.add(new HashSet<>(Arrays.asList("irrrrr","horaa","quiero","pasa")));
    itemsetList.add(new HashSet<>(Arrays.asList("rapido","insignificantes","vida","preocuparse","pasa","cosas")));
    itemsetList.add(new HashSet<>(Arrays.asList("pedir","ayer","pasa","deseo","avisaron","registro","saque","listo","foto","pasaporte","civil","huea","xq")));
    itemsetList.add(new HashSet<>(Arrays.asList("pronto","ojala","pasa","muero","recuperes")));
    itemsetList.add(new HashSet<>(Arrays.asList("@lucasvarela98","eee","felicidades","esaaa","motoraaa","amigo")));
    itemsetList.add(new HashSet<>(Arrays.asList("tomando","concejos","@lobosphinctes","tuiter","nota","amigo","jajaja")));
    itemsetList.add(new HashSet<>(Arrays.asList("tira","talla","@mcasascordero","jajajajaja","buena","amigo")));
    itemsetList.add(new HashSet<>(Arrays.asList("tema","quiero","amigo","entiendo")));
    itemsetList.add(new HashSet<>(Arrays.asList("subir","critica","rueda","juntar","costo","palos","mente","fuego","amigo","progre","massita","laboral","@jlespert")));
    itemsetList.add(new HashSet<>(Arrays.asList("monumento","matematica","feli","onda","apruebo","buena")));
    itemsetList.add(new HashSet<>(Arrays.asList("sol","amor","trate","tengas","mejor","suerte","buena")));
    itemsetList.add(new HashSet<>(Arrays.asList("barudy","jorge","jornada","ezzati","ucsc","ricardo","edificio","buena","monsenor")));
    itemsetList.add(new HashSet<>(Arrays.asList("pulgar","inversion","catolica","refuerzos","eric","merece","saludos","#24hdeportes","carlitos","villanueva","buena","10")));
    itemsetList.add(new HashSet<>(Arrays.asList("arreglen","@gransopi","bueeenaaa","vieja","pata","buena","poto")));
    itemsetList.add(new HashSet<>(Arrays.asList("@catitastar","@umusicchile","@dulcemaria","buena")));
    itemsetList.add(new HashSet<>(Arrays.asList("queda","pone","digo","intensa","personajes","parte","musica","hablando","personaje","resto","muestra")));
    itemsetList.add(new HashSet<>(Arrays.asList("queda","@sangreespanola")));
    itemsetList.add(new HashSet<>(Arrays.asList("queda","@jcoronelc","calama","teatro","presentamos")));
    itemsetList.add(new HashSet<>(Arrays.asList("queda","roja","digo","cara","wacha","pega","cachetazo")));
    itemsetList.add(new HashSet<>(Arrays.asList("queda","cara","temblando","besas","sospecas")));
    itemsetList.add(new HashSet<>(Arrays.asList("luz","queda","fresco","uh","baires","ushuaia","fresquito","dia","noche","borde")));
    itemsetList.add(new HashSet<>(Arrays.asList("100","mil","queda","comprar","ojo","supermercados","subieron","@horadeltaco","entregan","precios","gifcard","familia")));
    itemsetList.add(new HashSet<>(Arrays.asList("@informe365","queda","fuerte","sismo","#via","maribel","zambrano","herida","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("queda","extrana","loock","nuevo","hola","@abelpintos")));
    itemsetList.add(new HashSet<>(Arrays.asList("piropean","cerca","perfume")));
    itemsetList.add(new HashSet<>(Arrays.asList("francisco","san","bahia","#temblor","estemos","#actualidad","catastrofe","#falladesanandres","crees","cerca")));
    itemsetList.add(new HashSet<>(Arrays.asList("francisco","san","bahia","#temblor","estemos","#actualidad","catastrofe","#falladesanandres","crees","cerca")));
    itemsetList.add(new HashSet<>(Arrays.asList("@radiohuancavilk","abril","200","replicas","terremoto","cerca","ecuador")));
    itemsetList.add(new HashSet<>(Arrays.asList("@ecuavisainforma","contabiliza","abril","200","replicas","#terremoto","cerca","ig")));
    itemsetList.add(new HashSet<>(Arrays.asList("casa","ocurra","papa","traer","mato","novia")));
    itemsetList.add(new HashSet<>(Arrays.asList("aztlann","gusto","records","papa","street","sismo","dipies","@youtube","pelon")));
    itemsetList.add(new HashSet<>(Arrays.asList("importa","olvidamos","papa","hombre","mujer","mama","cn","libre","apasionados","#rapidodiunamentira")));
    itemsetList.add(new HashSet<>(Arrays.asList("#friuli1976","zamberletti","anni","#prociv","ando","papa","voleva","@pompieri","terremoto","nacque","40")));
    itemsetList.add(new HashSet<>(Arrays.asList("tardes","broche","papa","oro","cocina")));
    itemsetList.add(new HashSet<>(Arrays.asList("@dosdepunta","bienvenido","aireeee","mza","papa","gianluca","programa","dedicado","flamante","@denisrosales82")));
    itemsetList.add(new HashSet<>(Arrays.asList("forma","ht","@yaywazzup","@daya","automatica","escribiendo","lissete17","@caty","#mahirarriesgad","@toygarisikli","insisto","nota","bilbao")));
    itemsetList.add(new HashSet<>(Arrays.asList("frankie","episodio","grace","atribui","earthquake","nota","1x6","#bancodeseries")));
    itemsetList.add(new HashSet<>(Arrays.asList("acercan","municipales","nota","haciendo")));
    itemsetList.add(new HashSet<>(Arrays.asList("#diadelamadre","retweet","@mujereslared","hizo","contenta","reportaje","martes","bonito","@tus","quede")));
    itemsetList.add(new HashSet<>(Arrays.asList("@marceloroldancl","#diadelamadre","@mujereslared","hizo","contenta","reportaje","martes","bonito","quede")));
    itemsetList.add(new HashSet<>(Arrays.asList("#diadelamadre","@nacional114","@mujereslared","hizo","contenta","reportaje","martes","bonito","quede")));
    itemsetList.add(new HashSet<>(Arrays.asList("secouristes","38","seisme","@lorientlejour","decores","parlement","#equateur","pays")));
    itemsetList.add(new HashSet<>(Arrays.asList("acciones","38","rescatistas","terremoto","paises","condecorados","ecuavisa")));
    itemsetList.add(new HashSet<>(Arrays.asList("acciones","#ev","38","rescatistas","terremoto","paises","condecorados")));
    itemsetList.add(new HashSet<>(Arrays.asList("secouristes","38","seisme","decores","equateur","parlement","pays")));
    itemsetList.add(new HashSet<>(Arrays.asList("22","56","deprem","greece","crete")));
    itemsetList.add(new HashSet<>(Arrays.asList("1976","22","friuli","tragedia","anni","19","domani","#specialestoria","terremoto","40","maggio","30")));
    itemsetList.add(new HashSet<>(Arrays.asList("22","#temblor","#chile","epicentro","profundidad","magnitud","tongoy")));
    itemsetList.add(new HashSet<>(Arrays.asList("22","anni","19","domani","1976","friuli","tragedia","#specialestoria","terremoto","@raistoria","40","maggio","30")));
    itemsetList.add(new HashSet<>(Arrays.asList("buen","cilloniz","jugado","#alaire","pase","metiche")));
    itemsetList.add(new HashSet<>(Arrays.asList("#video","cilloniz","@janetbarbozaa","janet","daniela","responde","barboza","@danicilloniz")));
    itemsetList.add(new HashSet<>(Arrays.asList("#video","cilloniz","@janetbarbozaa","janet","daniela","responde","barboza","@danicilloniz")));
    itemsetList.add(new HashSet<>(Arrays.asList("@pdc","informacion","pdteviktor","leer","@marciaperez2016","@mileschile","hungria","@minuciorufus","orban","chile","interesante")));
    itemsetList.add(new HashSet<>(Arrays.asList("informacion","plastica","argila","mayor","click","beneficios")));
    itemsetList.add(new HashSet<>(Arrays.asList("chicoz","chicox","#noncadiamomai","alguien","explique","@weblodovicavene","temblando","dnmdks","creoo")));
    itemsetList.add(new HashSet<>(Arrays.asList("chicoz","chicox","#noncadiamomai","alguien","explique","@weblodovicavene","temblando","dnmdks","creoo")));
    itemsetList.add(new HashSet<>(Arrays.asList("termine","ver","alguien","explique","@catazanardo","busco","serie","dia")));
    itemsetList.add(new HashSet<>(Arrays.asList("responde","agus","haggggggo")));
    itemsetList.add(new HashSet<>(Arrays.asList("rica","roja","#chiloeestaprivao","brava","#chiloeencrisis","marea","#carelmapu","#maullin","sector","mar")));
    itemsetList.add(new HashSet<>(Arrays.asList("val","entel","vina","region","mar")));
    itemsetList.add(new HashSet<>(Arrays.asList("finales","san","gonzalo","posiciones","pts","campo","zona","caih","venado","pompeya","mza","club","chapaleufu","tuerto")));
    itemsetList.add(new HashSet<>(Arrays.asList("swimming","providencia","rooms","living","aeroplanes","spa","metropolitana","club","pools","region")));
    itemsetList.add(new HashSet<>(Arrays.asList("jajaajja","@leandiazz","club","ratito","enganar","jajaja")));
    itemsetList.add(new HashSet<>(Arrays.asList("interprete","tremblement","terre","club","dorothee","compil","ecoutez")));
    itemsetList.add(new HashSet<>(Arrays.asList("escuchando","tronic")));
    itemsetList.add(new HashSet<>(Arrays.asList("escuchando","mierda","viejo","musica","cabeza","daaaaaa")));
    
    itemsetList.add(new HashSet<>(Arrays.asList("من"," #العراق"," في"," بسبب")));
    itemsetList.add(new HashSet<>(Arrays.asList("#العراق"," في")));
    itemsetList.add(new HashSet<>(Arrays.asList("العراق")));
    itemsetList.add(new HashSet<>(Arrays.asList("من"," #العراق"," @wesal"," rsd"," #وحدة"," محمد"," بث"," تقي"," المرجع"," استمرار"," #وصال"," الرصد"," الشيعي"," #الكويت"," #السعودية"," المدرسي"," قناة"," انزعاج")));
    itemsetList.add(new HashSet<>(Arrays.asList("العفو"," من"," بممارسة"," مجموعه"," الشعب"," @amr1020"," العراق"," بمباركة"," وعهرهم"," #قانون"," هدر"," احتلوا"," وقاحه"," العراقي"," للتضحيات"," السفله"," وبدأو"," سفالتهم"," بكل")));
    itemsetList.add(new HashSet<>(Arrays.asList("العفو"," من"," بممارسة"," مجموعه"," الشعب"," @amr1020"," العراق"," بمباركة"," وعهرهم"," #قانون"," هدر"," احتلوا"," وقاحه"," العراقي"," للتضحيات"," السفله"," وبدأو"," سفالتهم"," بكل")));
    itemsetList.add(new HashSet<>(Arrays.asList("من"," العراق")));
    itemsetList.add(new HashSet<>(Arrays.asList("#العراق"," من"," في"," #السعودية")));
    itemsetList.add(new HashSet<>(Arrays.asList("من"," #العراق"," @wesal"," rsd"," #وحدة"," محمد"," بث"," تقي"," المرجع"," استمرار"," #وصال"," الرصد"," الشيعي"," #الكويت"," #السعودية"," المدرسي"," قناة"," انزعاج")));
    itemsetList.add(new HashSet<>(Arrays.asList("العفو"," العراق"," بسبب"," #قانون"," هدر")));
    itemsetList.add(new HashSet<>(Arrays.asList("العراق")));
    itemsetList.add(new HashSet<>());
    itemsetList.add(new HashSet<>());
    itemsetList.add(new HashSet<>());
    itemsetList.add(new HashSet<>());
    itemsetList.add(new HashSet<>());
    itemsetList.add(new HashSet<>());
    itemsetList.add(new HashSet<>());
    
    TUtilsDescriber.cleanZiptTail(itemsetList, prop);
    List<Map.Entry<Set<String>, Double>> result_filtered = BurstyGenerator.frequenItemSets(itemsetList, prop);
    
    System.out.println(result_filtered.size());
    
    int i = 1;
    for (Map.Entry<Set<String>, Double> entry : result_filtered) {
      System.out.printf("%2d: %9s, support: %1.3f\n",
                        i++, 
                        entry.getKey(),
                        entry.getValue());
    }
    
  }

}

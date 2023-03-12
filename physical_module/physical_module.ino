#include <WiFiNINA.h>
#include <HttpClient.h>
#include <ArduinoJson.h>
#include <Adafruit_Fingerprint.h>
#include "src/Epd/epd1in54_V2.h"
#include "src/Epd/epdpaint.h"
#include "img/finger_img.h"

#define COLORED 0
#define UNCOLORED 1

WiFiClient client;

String HTTP_IP;
uint16_t HTTP_PORT;
uint16_t AUTH_TOKEN_ID = 0;
String AUTH_TOKEN_VALUE;
String AUTH_TOKEN_STATE = "GENERATED";
uint8_t AUTH_TOKEN_TIME = 0;
uint8_t f_buf[512];

void(* resetFunc) (void) = 0;

// -------------------------------------------------------------------------------------------------------------------------------------
//  Wyświetlacz
// -------------------------------------------------------------------------------------------------------------------------------------
enum 
{
  SCREEN_PAGE_WIFI,
  SCREEN_PAGE_INPUT,
  SCREEN_PAGE_TOKEN,
  SCREEN_PAGE_REG,
  SCREEN_PAGE_LOG
};

static void displayPage(uint8_t page_num, const char* text)
{
  Epd epd;
  unsigned char image[1024];
  Paint paint(image, 0, 0);

  switch (page_num) 
  {
    case SCREEN_PAGE_WIFI:
      epd.LDirInit();
      epd.Clear();
      paint.SetWidth(200);
      paint.SetHeight(30);

      paint.Clear(COLORED);
      paint.DrawStringAt(0, 8, "CONNECTING TO:", &Font20, UNCOLORED);
      epd.SetFrameMemory(paint.GetImage(), 0, 50, paint.GetWidth(), paint.GetHeight());

      paint.Clear(UNCOLORED);
      paint.DrawStringAt(0, 0, text, &Font20, COLORED);
      epd.SetFrameMemory(paint.GetImage(), 0, 90, paint.GetWidth(), paint.GetHeight());

      paint.Clear(UNCOLORED);
      paint.DrawStringAt(0, 0, "..............", &Font20, COLORED);
      epd.SetFrameMemory(paint.GetImage(), 0, 130, paint.GetWidth(), paint.GetHeight());

      epd.DisplayFrame();
      break;

    case SCREEN_PAGE_INPUT:
      epd.LDirInit();
      epd.Clear();
      paint.SetWidth(200);
      paint.SetHeight(30);

      paint.Clear(COLORED);
      paint.DrawStringAt(0, 8, "INPUT DATA:", &Font20, UNCOLORED);
      epd.SetFrameMemory(paint.GetImage(), 0, 50, paint.GetWidth(), paint.GetHeight());

      paint.Clear(UNCOLORED);
      paint.DrawStringAt(0, 0, "192.168.4.1", &Font20, COLORED);
      epd.SetFrameMemory(paint.GetImage(), 0, 90, paint.GetWidth(), paint.GetHeight());

      epd.DisplayFrame();
      break;  

    case SCREEN_PAGE_TOKEN:
      if(AUTH_TOKEN_TIME == 0)
      {
        epd.LDirInit();
        epd.DisplayPartBaseImage(finger_img);
      }
      paint.SetWidth(120);
      paint.SetHeight(60);

      paint.Clear(UNCOLORED);
      paint.DrawStringAt(17, 10, text, &Font20, COLORED);
      paint.DrawRectangle(0, 0, 117, 35, COLORED);
      paint.DrawRectangle(0, 35, 117, 50, COLORED);
      paint.DrawFilledRectangle(0, 35, 117 - AUTH_TOKEN_TIME, 50, COLORED);
      epd.SetFrameMemoryPartial(paint.GetImage(), 40, 140, paint.GetWidth(), paint.GetHeight());

      epd.DisplayPartFrame();  
      break;  

    case SCREEN_PAGE_REG:
      epd.LDirInit();
      epd.Clear();
      paint.SetWidth(200);
      paint.SetHeight(30);

      paint.Clear(COLORED);
      paint.DrawStringAt(0, 8, "REGISTRATION", &Font20, UNCOLORED);
      epd.SetFrameMemory(paint.GetImage(), 0, 50, paint.GetWidth(), paint.GetHeight());

      paint.Clear(UNCOLORED);
      paint.DrawStringAt(0, 0, "SCAN FINGER", &Font20, COLORED);
      epd.SetFrameMemory(paint.GetImage(), 0, 90, paint.GetWidth(), paint.GetHeight());

      paint.Clear(UNCOLORED);
      paint.DrawStringAt(0, 0, "TWICE", &Font20, COLORED);
      epd.SetFrameMemory(paint.GetImage(), 0, 110, paint.GetWidth(), paint.GetHeight());

      epd.DisplayFrame();
      break;  

    case SCREEN_PAGE_LOG:
      epd.LDirInit();
      epd.Clear();
      paint.SetWidth(200);
      paint.SetHeight(30);

      paint.Clear(COLORED);
      paint.DrawStringAt(0, 8, "LOGIN", &Font20, UNCOLORED);
      epd.SetFrameMemory(paint.GetImage(), 0, 50, paint.GetWidth(), paint.GetHeight());

      paint.Clear(UNCOLORED);
      paint.DrawStringAt(0, 0, "SCAN FINGER", &Font20, COLORED);
      epd.SetFrameMemory(paint.GetImage(), 0, 90, paint.GetWidth(), paint.GetHeight());

      epd.DisplayFrame();
      break;  

    default:
      break;
  } 

  delay(2000);
}



// -------------------------------------------------------------------------------------------------------------------------------------
//  Zapisywanie i czytanie z pamięci danych połączeniowych
// -------------------------------------------------------------------------------------------------------------------------------------
String readCredentials()
{
  WiFiStorageFile file = WiFiStorage.open("/fs/secrets");
  uint8_t rlen = file.available();
  char buf[rlen];

  file.read(buf, rlen);
  file.close();

  return String(buf);
}

void writeCredentials(String str)
{
  WiFiStorageFile file = WiFiStorage.open("/fs/secrets");
  if (file) 
  {
    file.erase();
  }
  file.write(str.c_str(), str.length());
}



// -------------------------------------------------------------------------------------------------------------------------------------
//  Rozdzielenie napisu podanych seperatorem
// -------------------------------------------------------------------------------------------------------------------------------------
String splitString(String data, char separator, uint16_t index)
{
  uint16_t found = 0;
  uint16_t strIndex[] = {0, -1};
  uint16_t maxIndex = data.length()-1;

  for(uint16_t i=0; i<=maxIndex && found<=index; i++)
  {
    if(data.charAt(i)==separator || i==maxIndex)
    {
        found++;
        strIndex[0] = strIndex[1]+1;
        strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
  }

  return found>index ? data.substring(strIndex[0], strIndex[1]) : "";
}



// -------------------------------------------------------------------------------------------------------------------------------------
//  Połączenie z siecią, start serwera webowego do wprowadzenia danych połączeniowych
// -------------------------------------------------------------------------------------------------------------------------------------
uint8_t connectToWifi(String ssid, String user, String pass) 
{
  Serial.println(F("=========================================="));
  uint8_t CONN_STATUS;
  Serial.print(F("Connecting to: "));
  Serial.println(ssid);

  if(user == "")
  {
    CONN_STATUS = WiFi.begin(ssid.c_str(), pass.c_str());
  }
  else
  {
    CONN_STATUS = WiFi.beginEnterprise(ssid.c_str(), user.c_str(), pass.c_str());
  }

  if(CONN_STATUS == WL_CONNECTED)
  {
    Serial.print(F("IP address: "));
    Serial.println(WiFi.localIP());
  }
  return CONN_STATUS;
}

void startWebServer()
{
  char name[] = "Biometric module"; 
  String credentials = ""; 
  uint8_t CONN_STATUS;
  WiFiServer server(80);

  Serial.print(F("Creating access point named: "));
  Serial.println(name);

  CONN_STATUS = WiFi.beginAP(name);
  if (CONN_STATUS != WL_AP_LISTENING) 
  {
    Serial.println(F("Creating access point failed"));
    while (true);
  }

  delay(7000);
  server.begin();
  
  Serial.print(F("SSID: "));
  Serial.println(WiFi.SSID());

  Serial.print(F("Open a browser to http://"));
  Serial.println(WiFi.localIP());


  while(true)
  {
    if (CONN_STATUS != WiFi.status())
    {
      CONN_STATUS = WiFi.status();
    }

    client = server.available();   

    if (client) 
    {                                    
      String currentLine = "";                
      while (client.connected()) 
      {  
        if (client.available()) 
        {            
          char c = client.read();                               
          if (c == '\n') 
          {  
            if (currentLine.length() == 0) 
            {
              client.println(F("HTTP/1.1 200 OK"));
              client.println(F("Content-type:text/html"));
              client.println();
              client.print(F("ENTER YOUR CREDENTIALS: "));
              client.print(F("<br> <form action=\"/post\"> ssid: <input type=\"text\" name=\"SSID\"> <br> user: <input type=\"text\" name=\"USER\"> <br> password: <input type=\"text\" name=\"PASS\"> <br> host: <input type=\"text\" name=\"IP\"> <br> port: <input type=\"text\" name=\"PORT\"> <br> <input type=\"submit\" value=\"Submit\"></form>"));
              client.println();
      
              break;
            }
            else 
            {    
              if(currentLine.startsWith("GET") && currentLine.endsWith("HTTP/1.1"))
              {
                credentials = currentLine.c_str();
              }
              currentLine = "";
            }
          }
          else if (c != '\r') 
          {  
            currentLine += c;   
          }
        }
      }
      client.stop();
      if(credentials.indexOf("SSID") > 0)
      {
        credentials.replace("GET /post?SSID=", "");
        credentials.replace("USER=", "");
        credentials.replace("PASS=", "");
        credentials.replace("IP=", "");
        credentials.replace("PORT=", "");
        credentials.replace(" HTTP/1.1", "");
        credentials.replace("&", ";");
        writeCredentials(credentials);
        break;
      }
    }
  }
} 



// -------------------------------------------------------------------------------------------------------------------------------------
//  Żądania do API
// -------------------------------------------------------------------------------------------------------------------------------------
void httpGetToken() 
{
  Serial.println(F("=========================================="));
  HttpClient http = HttpClient(client, HTTP_IP, HTTP_PORT);
  http.get("/api/tokens/generate");

  StaticJsonDocument<70> doc;
  deserializeJson(doc, http.responseBody());
  const char* token_value = doc["token_value"];
  uint16_t token_id = doc["token_id"];
  client.stop();

  Serial.print(F("Token: "));
  Serial.println(token_value);

  AUTH_TOKEN_VALUE = token_value;
  AUTH_TOKEN_ID = token_id;
}

void httpCheckTokenState() 
{
  HttpClient http = HttpClient(client, HTTP_IP, HTTP_PORT);
  http.get("/api/tokens/" + String(AUTH_TOKEN_ID) + "/state");

  StaticJsonDocument<50> doc;
  deserializeJson(doc, http.responseBody());
  const char* token_state = doc["token_state"];
  client.stop();

  AUTH_TOKEN_STATE = token_state;
  if(AUTH_TOKEN_STATE == "")
  {
    resetFunc();
  }
}

void httpUpdateTokenState(const char* token_state) 
{
  HttpClient http = HttpClient(client, HTTP_IP, HTTP_PORT);
  http.put("/api/tokens/" + String(AUTH_TOKEN_ID) + "/state", "application/json", "{\"token_state\":\"" + String(token_state) + "\"}");
  http.responseStatusCode();
  client.stop();
}

void httpSaveFingerprintTemplate()
{
  String finger_template = "{\"template\":\"";
  for (uint16_t i = 0; i < 512; i++) 
  {
    finger_template += (String(f_buf[i]) + " ");
  }
  finger_template += "\"}";

  HttpClient http = HttpClient(client, HTTP_IP, HTTP_PORT);
  http.post("/api/registration/" + String(AUTH_TOKEN_ID) + "/fingerprint", "application/json", finger_template);
  Serial.println("Registered");
  client.stop();
}

void httpGetFingerprintTemplate()
{
  HttpClient http = HttpClient(client, HTTP_IP, HTTP_PORT);
  http.get("/api/login/" + String(AUTH_TOKEN_ID) + "/fingerprint");

  String finger_template = http.responseBody();
  finger_template.replace("{\"template\":\"", "");
  finger_template.replace(" \"}", "");
  client.stop();

  for (int i = 0; i < 512; i++) 
  { 
    f_buf[i] = (uint8_t)(splitString(finger_template, ' ', i).toInt());
  }
}



// -------------------------------------------------------------------------------------------------------------------------------------
//  Rejestracja użytkownika
// -------------------------------------------------------------------------------------------------------------------------------------
void registration() 
{
  Serial.println(F("======================================="));
  Adafruit_Fingerprint sensor = Adafruit_Fingerprint(&Serial1);

  sensor.begin(57600);
  delay(5);
  if (!sensor.verifyPassword()) 
  {
    Serial.println(F("Did not find fingerprint sensor :("));
    while (1) { delay(1); }
  }

  sensor.emptyDatabase();
  addNewFingerprint(sensor); 
  getFingerprintTemplate(sensor);
  httpSaveFingerprintTemplate();
}

void addNewFingerprint(Adafruit_Fingerprint sensor) 
{
  uint16_t p = -1;
  Serial.println("Waiting for valid finger");
  while (p != FINGERPRINT_OK) 
  {
    p = sensor.getImage();
    if(p == FINGERPRINT_OK)
    {
      Serial.println("Image taken");
      break;
    }
  }
  sensor.image2Tz(1);

  delay(2000);
  p = 0;
  while (p != FINGERPRINT_NOFINGER) 
  {
    p = sensor.getImage();
  }

  p = -1;
  Serial.println("Place same finger again");
  while (p != FINGERPRINT_OK) 
  {
    p = sensor.getImage();
    if(p == FINGERPRINT_OK)
    {
      Serial.println("Image taken");
      break;
    }
  }

  sensor.image2Tz(2);
  sensor.createModel();
  sensor.storeModel(1);
}

void getFingerprintTemplate(Adafruit_Fingerprint sensor) 
{
  sensor.loadModel(1);
  sensor.getModel();
  sensor.get_template_buffer(512, f_buf);
}



// -------------------------------------------------------------------------------------------------------------------------------------
//  Logowanie użytkownika
// -------------------------------------------------------------------------------------------------------------------------------------
void login() 
{
  Serial.println(F("======================================="));
  Adafruit_Fingerprint sensor = Adafruit_Fingerprint(&Serial1);

  sensor.begin(57600);
  delay(5);
  if (!sensor.verifyPassword()) 
  {
    Serial.println(F("Did not find fingerprint sensor :("));
    while (1) { delay(1); }
  }

  sensor.emptyDatabase();
  httpGetFingerprintTemplate();
  saveFingerprintTemplate(sensor);
  if(matchFingerprintTemplate(sensor) == FINGERPRINT_OK)
  {
    httpUpdateTokenState("LOGIN");
  }
  else
  {
    httpUpdateTokenState("INVALID_LOGIN");
  }
}

void saveFingerprintTemplate(Adafruit_Fingerprint sensor) 
{
  if (sensor.write_template_to_sensor(512, f_buf)) 
  { 
    Serial.println(F("Writing to sensor..."));
  } 
  else 
  {
    Serial.println(F("Writing to sensor failed"));
    return;
  }
  sensor.storeModel(1);
}

uint8_t matchFingerprintTemplate(Adafruit_Fingerprint sensor) 
{
  uint16_t p = -1;
  Serial.println(F("Waiting for valid finger"));
  while (p != FINGERPRINT_OK) 
  {
    p = sensor.getImage();
    if(p == FINGERPRINT_OK)
    {
      Serial.println(F("Image taken"));
      break;
    }
  }
  sensor.image2Tz(1);

  p = sensor.fingerSearch();
  if (p == FINGERPRINT_OK) 
  {
    Serial.print(F("Match with confidence of ")); Serial.println(sensor.confidence);
  }

  return p;
}



// -------------------------------------------------------------------------------------------------------------------------------------
//  Funkcje główne
// -------------------------------------------------------------------------------------------------------------------------------------
void setup() 
{
  Serial.begin(9600);  
  while (!Serial) {;}

  String credentials = readCredentials();
  HTTP_IP = splitString(credentials, ';', 3).c_str();
  HTTP_PORT = splitString(credentials, ';', 4).toInt();

  displayPage(SCREEN_PAGE_WIFI, splitString(credentials, ';', 0).c_str());
  uint8_t CONN_STATUS = connectToWifi(splitString(credentials, ';', 0), splitString(credentials, ';', 1), splitString(credentials, ';', 2));
  httpGetToken();

  if(CONN_STATUS != WL_CONNECTED || AUTH_TOKEN_VALUE == "")
  {
    displayPage(SCREEN_PAGE_INPUT, "");
    startWebServer();
    delay(3000);
    resetFunc();
  }
}

void loop() 
{  
  if(AUTH_TOKEN_STATE == "PENDING_REGISTRATION")
  {
    displayPage(SCREEN_PAGE_REG, "");
    registration();
    AUTH_TOKEN_STATE = "";
  }
  else if(AUTH_TOKEN_STATE == "PENDING_LOGIN")
  {
    displayPage(SCREEN_PAGE_LOG, "");
    login();
    AUTH_TOKEN_STATE = "";
  }

  if(AUTH_TOKEN_STATE == "" || AUTH_TOKEN_TIME >= 120)
  {
    httpGetToken();
    AUTH_TOKEN_TIME = 0;
  }
  else
  {
    displayPage(SCREEN_PAGE_TOKEN, AUTH_TOKEN_VALUE.c_str());
    AUTH_TOKEN_TIME += 10;
    delay(8500);
  }

  httpCheckTokenState();
}

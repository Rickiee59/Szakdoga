# OtthonAPPolás
Szakdolgozat - UUH869\
ReadMe WIP

## 2 részből áll az app:

### 1. Mobil oldala

Az appban van 2 fül: az egyik egy lista azokról a betegekről, akik következnek majd, és a másik a lista a már ledolgozott napokról
#### Beteg lista
- Rányom a betegre/a műszakra, ott lesz a címe, elérhetősége, adatai és leírása
- Amikor az ápoló megérkezik a beteghez, az applikációban becsekkol, hogy megjött, és elkezdi a kezelést
- Az app ezt regisztrálja az ápolóhoz egy adatbázisban
- A kezelés végén meg kell adni egy digitális aláírást a betegnek VAGY egy képet a betegről az ápolónak (ez még nem fix melyik), és csak utána engedi lezárni a kezelést/műszakot
- Ezeket az adatokat elküldi megint az adatbázisba, a végidővel együtt
- Erről készít egy bizonylatot pdf-ben
- Kiszámolja a bérét az ápolónak aznapra, és a betegnek a kezelés költségét és hozzáadja a hónaphoz
- Figyelembe veszi a hétvégéket, ünnepnapokat és ehhez mérten számolja ki a pénz mennyiséget
- Ez megjelenik a másik listán
#### Múlt műszakok lista
- Visszanézheti mikor dolgozott, mennyit, hol, kinél stb...
- Ezekre külön szűrést és rendezést lehet tenni bizonyos szempontok alapján 

### 2. Desktop oldala
- A közös adatbázisból, ahova az app feltölti az adatokat, azokat az adminisztrátor le tudja kérni egy windows desktop appban (winform pl.)
- Ez alapján összegzi a hónapot az ápolókra és a betegekre nézve
- Az ápolóknak a havi bérét lehet számlázni és a betegeknek meg a kezelés költségét
  

# Casinomoninpeli väliraportti

## Kuvaus

Mahdollistaa blackjack pelistä kiinnostuneiden ryhmien yhteisen pelaamisen ja kommunikoinnin. Luo lisäarvoa tarjoamalla ymmärrystä pelin toiminnasta järjestelmän kortin ennustus, suositus ja tilasto ominaisuuksilla.

Blackjack missä peli pelataan palvelimella (palvelinohjelma) ja samaan peliin pystyy liittymään useampi pelaaja (asiakasohjelma).

Pelissä voi olla mukana pelaajana tai sitten vain katsojana milloin pystyy näkemään pelin tilan, mutta ei ole mukana pelaamassa.

Pelin aikana tallennetaan tilastotietoa tietokantaan. Nämä tilastot haetaan aina jokaisen blackjack kierroksen lopussa ja näytetään pelaajille.

(Tästä alkavaa ei ole toteutettu)

Pelin aikana pelaajat pystyvät keskustelemaan toistensa kanssa peliin liittyvässä chatti ikkunassa.

Pelissä on kortinlaskija moduuli, mikä lähettää pelaajille ennustuksen seuraavasta pakassa olevasta kortista ja ehdotuksen siitä mitä pelaajan kannattaisi tehdä seuraavaksi.

## Tällä hetkellä sovelluksessa pystyy

- Liittymään palvelimella olevaan peliin muiden pelaajien kanssa (Tarina: Pelaajana pystyn liittymään peliin).
- Pelaamaan blackjack peliä (Tarinat: Pelaajana pystyn näkemään pelin tämänhetkisen tilan, Pelaajana pystyn valitsemaan, otanko uuden kortin vai jäänkö, Pelaajana pystyn "tuplaamaan")
- Pelaamaan useamman kierroksen ja jäämään katsojaksi (Tarina: Pelaajana pystyn valitsemaan pelaanko seuraavan kierroksen vai jäänkö katsojaksi)
- Asettamaan panoksen peliin, voittamaan pisteitä ja häviämään pisteitä (Tarina: Pelaajana pystyn lyömään vetoa oman pelin kulusta)
- Näkemään tilastoja peleistä (Tarina: Pelaajana pystyn näkemään tilastoja)

## Arkkitehtuuri

Sovellus on ensimmäiseksi jaettu kahteen osaan: server ja client.

Serveriin kuuluu kolme moduulia: peli/logiikka (logic.java), kommunikoinnin hoitava moduuli (ServerListener rajapinta ja kaikki Connection alkuiset luokat) sekä tietokanta moduuli (DatabaseInterface.java, Statistic.java). Peli moduuli käyttää ServerListener rajapintaa ja tietokanta moduulia (ei rajapintaa).

Client käyttää MVC mallia: ClientConnection (model), ClientView rajapinta (view), ClientController (controller). Näistä ClientConnection tekee käyttöliittymä päivityksiä ClientView rajapinnan kautta ja GUIView (ClientView toteuttaja) kutsuu ClientController luokkaa käyttäjän haluammalla tavalla mikä kutsuu ClientConnection luokkaa. Model on käytännössä palvelinohjelmisto, ClientConnection luokka hakee kaiken tiedon serveriltä. Clientin tehtävä on vain ja ainoastaan näyttää peli käyttäjälle ja välittää käyttäjän toiminta serverille.

Tämän lisäksi sovellukseen kuuluu yleisiä luokkia mitä käyttäjät molemmat server ja client. Näihin kuuluu utility luokat (Määrittelevät tietoa pelissä esim. Card.java PlayerHand.java Player.java) ja communication luokat (Määrittelevät tiedon mikä liikkuu serverin ja clientin välillä ja toteuttavat siis Serializable rajapinnan).

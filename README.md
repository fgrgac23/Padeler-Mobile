# Padeler

## Projektni tim

Ime i prezime | E-mail adresa (FOI) | JMBAG | Github korisničko ime | Seminarska grupa
------------  | ------------------- | ----- | --------------------- | ----------------
Filip Grgac | fgrgac23@foi.hr | 0016167082 | fgrgac23 | G01
Karlo Kršak | kkrsak23@foi.hr | 0016165894 | kkrsak23 | G01
Kristian Katulić | kkatulic23@foi.hr | 0016168011 | kkatulic23 | G01

## Opis domene
Softversko rješenje za pronalazak partnera za padel.

## Specifikacija projekta
Aplikacija Padeler zamišljena je kao mobilna aplikacija razvijena u Android Studiju pomoću Kotlin programskog jezika. Cilj aplikacije je povezati padel igrače na temelju njihove lokacije i razine igre. Korisnici mogu izrađivati i uređivati profile, pretraživati druge igrače u blizini, “swipeati” za odabir partnera, dopisivati se i davati ocjene nakon mečeva.

Aplikacija se sastoji od mobilnog (frontend) i poslužiteljskog (backend) dijela.
Mobilna aplikacija, izrađena u Kotlinu, zadužena je za korisničko sučelje i komunikaciju s poslužiteljem. Ona sadrži module za prijavu i registraciju korisnika, upravljanje profilom, prikaz i filtriranje igrača u blizini pomoću GPS-a, “swipe” sustav za podudaranje, chat modul za komunikaciju te sustav obavijesti o novim porukama i podudaranjima.

Oznaka | Naziv | Kratki opis | Odgovorni član tima
------ | ----- | ----------- | -------------------
| F01 | Autentikacija | Omogućuje novim korisnicima kreiranje računa putem e-mail adrese. Sustav validira unesene podatke i sprema ih u bazu. Zatim se korisnici prijavljuju pomoću registriranih podataka. Sustav provjerava točnost unesenih podataka i omogućuje siguran pristup aplikaciji. | Karlo Kršak |
| F02 | Uređivanje korisničkog profila | Korisnik može ažurirati osobne podatke, dodati ili promijeniti profilnu sliku, opis, razinu vještine, lokaciju i preferencija za igru. | Kristian Katulić |
| F03 | Geolokacijsko pretraživanje | Aplikacija prikazuje druge padel igrače u blizini korisnikove lokacije, s mogućnošću podešavanja radijusa pretrage. | Karlo Kršak |
| F04 | Sustav “swipe” interakcije | Korisnik može “swipeati” desno ako želi igrati s nekim, ili lijevo za preskakanje. Ako oba igrača “swipeaju” desno, stvara se podudaranje (match). | Filip Grgac |
| F05 | Sustav poruka (chat) | Nakon što se ostvari “match”, korisnici mogu odabrati ikonu WhatsApp aplikacije koja će korisnike odvesti na WhatsApp i tamo će moći komunicirati. | Kristian Katulić |
| F06 | Filtar za pretragu partnera | Korisnik može filtrirati potencijalne partnere po kriterijima kao što su razina vještine, spol, lokacija i dostupnost. | Karlo Kršak |
| F07 | Sustav obavijesti | Aplikacija šalje push obavijesti o novim porukama i podudaranjima. Korisnik može prilagoditi postavke obavijesti. | Filip Grgac |
| F08 | Ocjenjivanje suigrača | Nakon odigranog meča korisnik može ocijeniti partnera i ostaviti komentar. | Kristian Katulić |
| F09 | Prijavljivanje korisnika | Korisnici mogu prijaviti neprimjereno ponašanje, te administrator može provjeriti prijave i poduzeti mjere (zabrana/kazna)  | Filip Grgac |

## Tehnologije i oprema
Android Studio, Kotlin i GitHub okruženje.

## Baza podataka i web server
Tražimo pristup serveru na kojemu ćemo moći imati bazu podataka.

## .gitignore
Uzmite u obzir da je u mapi Software .gitignore konfiguriran za nekoliko tehnologija, ali samo ako će projekti biti smješteni direktno u mapu Software ali ne i u neku pod mapu. Nakon odabira konačne tehnologije i projekta obavezno dopunite/premjestite gitignore kako bi vaš projekt zadovoljavao kriterije koji su opisani u ReadMe.md dokumentu dostupnom u mapi Software.

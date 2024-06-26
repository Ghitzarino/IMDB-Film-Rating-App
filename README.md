README IMDB – PROIECT POO
			-Ghiță Alexandru

Acest proiect reprezinta implementarea unei aplicatii stil IMDB, care prezinta un terminal 
complet functional cu posibilitatea de testare a tuturor functionalitatilor si in plus salvarea 
modificarilor asupra fisierelor json si inceperea urmatoarei rulari cu informatiile nou salvate, 
cat si o interfata grafica functionala, dar care nu prezinta absolut toate functionalitatile, 
ci demonstreaza mai mult capabilitatea de conectare a backend-ului si frontend-ului.

Consider ca a fost un proiect cu un grad dificil de dificultate dpdv al timpului, necesitand 
aproximativ 50 de ore de lucru pe parcursul a unui interval de 2 saptamani (de cand m-am apucat si 
pana am terminat tema), dar cu un grad mediu al dificultatii dpdv al complexitatii, neavand niste 
elemente pe care sa nu le inteleg sau pe care sa stau un timp indelungat pentru a imi da seama cum 
se folosesc si cum se implementeaza.

In acest proiect am implementat toate clasele descrise folosind principiul incapsularii si le-am 
folosit cu o instanta Singleton in clasa IMDB (clasa principala a proiectului) pentru a le stoca 
din parsarea json-urilor (facuta folosind biblioteca Jackson) in niste liste. Am folosit UserFactory 
pentru instantierea si crearea obiectelor de tip User. De asemenea, clasa User contine si campul 
Information care este creat folosing design pattern-ul Builder. Tot pentru clasa User am creat si un 
design pattern Strategy de care m-am folosit pentru selectarea metodei de incrementare a experientei 
utilizatorului. Pe langa acestea, Userii faceau parte si din gruparea de Observers pentru care avea 
ca Subjects obiecte din clasele Rating, Review si Production (pentru a notifica utilizatorul creator 
cand primeste o recenzie productia adaugata de el).

Functionalitatile merg conform specificatiilor din enunt si au capacitatea de a fi testate prin 
terminal. Ca si bonus am adaugat mai multe modalitati de sortare si filtrare a productiilor, iar 
la sfarsitul rularii aplicatiei, schimbarile sunt salvate in fisiere json, din care la urmatoare 
rulare pot fi parsate din nou cu succes pentru a oferi capacitatea de stocare reala a datelor 
in folosirea aplicatiei.

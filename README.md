# PowerHome : application Android d'une résidence participative

Projet du module R4.Real.11 (Développement d'applications mobiles, BUT 2 Informatique, IUT de Paris - Rives de Seine), rendu le 27 mars 2026.

Les résidents consultent les habitats de la résidence, gèrent les équipements de leur propre habitat et réduisent la consommation d'énergie collective en réservant des créneaux horaires, avec un système de bonus / malus en « éco-coins ».

## Fonctionnalités

- **Authentification distante** : connexion et inscription, token stocké dans les `SharedPreferences`. Après l'inscription, l'utilisateur crée un habitat ou demande à en rejoindre un.
- **Liste des habitats** : chargée depuis l'API (JSON), avec résident, étage, surface et icônes des équipements. Le type d'appareil est reconnu par mot-clé.
- **Navigation Drawer** : habitats, mon habitat, mes requêtes, calendrier, mes réservations, préférences, déconnexion.
- **Mon habitat** : informations personnelles, liste des appareils, ajout d'un appareil.
- **Demandes et notifications** : envoi d'une demande pour rejoindre un habitat, réponse du propriétaire.
- **Calendrier éco-citoyen** : créneaux d'une heure (6h à 23h) sur 7 jours glissants, colorés selon le taux de charge.
- **Préférences** : thème sombre, couleur d'accent, langue, luminosité.

### Règle des éco-coins

| Taux de charge du créneau | Effet |
|---|---|
| Vert (0 à 30 %) | +10 éco-coins (bonus) |
| Orange (31 à 70 %) | 0 (neutre) |
| Rouge (plus de 70 %) | −10 éco-coins (malus) |

Le solde de départ est de 100 éco-coins. Une annulation rembourse la réservation.

## Architecture

```
Application Android (Java, XML, Volley)
        │  HTTP / JSON
        ▼
API REST PHP (login.php, register.php, getHabitats.php, bookSlot.php, getCalendar.php, ...)
        │
        ▼
Base MySQL powerhome_db
```

Tables : `user`, `habitat`, `appliance`, `timeslot`, `booking`, `habitat_resident`, `habitat_request`, `notification`.

## Technologies

- Android : Java, XML, Volley, DrawerLayout, fragments
- Serveur : PHP, MySQL, WAMP (environnement de test)
- Outil : Android Studio

## Lancer le projet

Prérequis : Android Studio, WAMP, un émulateur Android ([À compléter : version minimale d'Android / SDK]).

1. Cloner le dépôt : `git clone [À compléter : URL du dépôt]`
2. Copier le dossier de l'API PHP dans le dossier `www` de WAMP.
3. Créer la base `powerhome_db` et importer [À compléter : nom du fichier .sql].
4. Dans l'application, l'adresse de l'API est `http://10.0.2.2/[À compléter : chemin de l'API]` (10.0.2.2 désigne la machine hôte depuis l'émulateur).
5. Ouvrir le projet Android dans Android Studio et lancer l'application.

Le trafic HTTP en clair est autorisé dans le Manifest (`usesCleartextTraffic`) uniquement pour les tests en local.

## Équipe

Projet réalisé en binôme :

- Nicolas Plaisance
- Lenny M.

## Limites et pistes d'amélioration

- Le token d'authentification est un simple MD5 : un token Bearer serait plus sûr.
- Notifications push pour prévenir d'un pic de consommation.
- Graphique de consommation hebdomadaire.

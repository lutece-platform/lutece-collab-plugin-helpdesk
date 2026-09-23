-- liquibase formatted sql
-- changeset helpdesk:init_db_helpdesk_data_sample.sql

--
-- Truncate (foreign key order)
--
DELETE FROM helpdesk_visitor_question;
DELETE FROM helpdesk_question_answer;
DELETE FROM helpdesk_ln_faq_theme;
DELETE FROM helpdesk_ln_faq_subject;
DELETE FROM helpdesk_theme;
DELETE FROM helpdesk_subject;
DELETE FROM helpdesk_faq;

--
-- helpdesk_faq
--
INSERT INTO helpdesk_faq (id_faq,name,description,role_key,workgroup_key) VALUES
 (1,'Espace fonctionnel Lutece','Foire aux questions destinée aux webmestres et utilisateurs : prise en main, utilisation des plugins, configuration du site et références.','none','all');
INSERT INTO helpdesk_faq (id_faq,name,description,role_key,workgroup_key) VALUES
 (2,'Espace technique Lutece','Foire aux questions destinée aux développeurs et exploitants : construction d''un site, migration vers Lutece 8, exploitation.','none','all');

--
-- helpdesk_subject
--
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (0,'',0,0);

-- FAQ 1 : Espace fonctionnel
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (1,'Prise en main',0,0);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (2,'Installation',1,0);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (3,'Premiers pas',1,1);

INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (4,'Plugins (utilisation)',0,1);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (5,'Plugin Forms',4,0);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (6,'Plugin Appointment (Rendez-vous)',4,1);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (7,'Workflow',4,2);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (8,'Plugin Blog',4,3);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (9,'Autres plugins',4,4);

INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (10,'Configuration du site',0,2);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (11,'Arborescence et pages',10,0);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (12,'Propriétés du site',10,1);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (13,'Droits, rôles et groupes de travail',10,2);

INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (14,'Références',0,3);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (15,'Changelogs',14,0);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (16,'Guides utilisateurs',14,1);

-- FAQ 2 : Espace technique
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (17,'Développement',0,4);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (18,'POM de site et build',17,0);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (19,'Migration vers Lutece 8',17,1);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (20,'Exploitation',0,5);

--
-- helpdesk_theme (thèmes du formulaire de contact, chacun routé vers une liste de diffusion)
--
INSERT INTO helpdesk_theme (id_theme,theme,id_mailing_list,id_parent,id_order) VALUES (0,'','1',0,0);

-- FAQ 1
INSERT INTO helpdesk_theme (id_theme,theme,id_mailing_list,id_parent,id_order) VALUES (1,'Prise en main','1',0,0);
INSERT INTO helpdesk_theme (id_theme,theme,id_mailing_list,id_parent,id_order) VALUES (2,'Plugins','1',0,1);
INSERT INTO helpdesk_theme (id_theme,theme,id_mailing_list,id_parent,id_order) VALUES (3,'Forms','1',2,0);
INSERT INTO helpdesk_theme (id_theme,theme,id_mailing_list,id_parent,id_order) VALUES (4,'Rendez-vous','1',2,1);
INSERT INTO helpdesk_theme (id_theme,theme,id_mailing_list,id_parent,id_order) VALUES (5,'Workflow','1',2,2);
INSERT INTO helpdesk_theme (id_theme,theme,id_mailing_list,id_parent,id_order) VALUES (6,'Blog','1',2,3);
INSERT INTO helpdesk_theme (id_theme,theme,id_mailing_list,id_parent,id_order) VALUES (7,'Configuration du site','1',0,2);
INSERT INTO helpdesk_theme (id_theme,theme,id_mailing_list,id_parent,id_order) VALUES (8,'Autre demande','1',0,3);

-- FAQ 2
INSERT INTO helpdesk_theme (id_theme,theme,id_mailing_list,id_parent,id_order) VALUES (9,'Développement','1',0,4);
INSERT INTO helpdesk_theme (id_theme,theme,id_mailing_list,id_parent,id_order) VALUES (10,'Exploitation','1',0,5);

--
-- helpdesk_ln_faq_subject (sujets racine uniquement)
--
INSERT INTO helpdesk_ln_faq_subject (id_faq,id_subject) VALUES (1,1);
INSERT INTO helpdesk_ln_faq_subject (id_faq,id_subject) VALUES (1,4);
INSERT INTO helpdesk_ln_faq_subject (id_faq,id_subject) VALUES (1,10);
INSERT INTO helpdesk_ln_faq_subject (id_faq,id_subject) VALUES (1,14);
INSERT INTO helpdesk_ln_faq_subject (id_faq,id_subject) VALUES (2,17);
INSERT INTO helpdesk_ln_faq_subject (id_faq,id_subject) VALUES (2,20);

--
-- helpdesk_ln_faq_theme (thèmes racine uniquement)
--
INSERT INTO helpdesk_ln_faq_theme (id_faq,id_theme) VALUES (1,1);
INSERT INTO helpdesk_ln_faq_theme (id_faq,id_theme) VALUES (1,2);
INSERT INTO helpdesk_ln_faq_theme (id_faq,id_theme) VALUES (1,7);
INSERT INTO helpdesk_ln_faq_theme (id_faq,id_theme) VALUES (1,8);
INSERT INTO helpdesk_ln_faq_theme (id_faq,id_theme) VALUES (2,9);
INSERT INTO helpdesk_ln_faq_theme (id_faq,id_theme) VALUES (2,10);

--
-- helpdesk_question_answer
--

-- Sujet 2 : Installation
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (1,'Quels sont les prérequis pour installer un site Lutece ?','<p>Un site Lutece 8 nécessite :</p><ul><li>un JDK 17 ou supérieur ;</li><li>Maven 3.8 ou supérieur pour construire le site ;</li><li>un serveur d''applications Jakarta EE 10 (Open Liberty, Tomcat 10.1 ou équivalent) ;</li><li>une base de données MySQL / MariaDB (PostgreSQL est également supporté).</li></ul><p>Le plus simple pour découvrir la plateforme reste d''utiliser les images Docker de démonstration décrites dans la rubrique <em>Installation</em> du wiki.</p>',2,1,'2025-09-02 09:15:00',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (2,'Comment lancer rapidement une démonstration avec les images Docker ?','<p>Des images Docker de démonstration sont publiées pour chaque site type (site vitrine, formulaires, rendez-vous...).</p><p>Il suffit de récupérer le fichier <code>docker-compose.yml</code> correspondant, puis d''exécuter <code>docker compose up</code>. Le site est ensuite accessible sur le port indiqué dans le fichier, et le back office sur <code>/jsp/admin/AdminLogin.jsp</code>.</p><p>Les identifiants de démonstration sont rappelés dans la page <em>Images docker de démonstration</em> du wiki. Ne les utilisez jamais en production.</p>',2,1,'2025-09-02 09:20:00',1);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (3,'Peut-on installer Lutece sur un poste Windows ?','<p>Oui. La page <em>Installation poste Windows</em> décrit l''installation d''un JDK, de Maven et de MariaDB, puis la construction du site avec <code>mvn lutece:site-assembly</code>.</p><p>Points d''attention fréquents sous Windows : la variable <code>JAVA_HOME</code> doit pointer sur le JDK (pas sur un JRE), et les chemins contenant des espaces sont à éviter pour le répertoire de travail Maven.</p>',2,1,'2025-09-02 09:25:00',2);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (4,'Où se trouve la configuration de connexion à la base de données ?','<p>Dans le fichier <code>WEB-INF/conf/db.properties</code> du site déployé. Les clés <code>portal.url</code>, <code>portal.user</code> et <code>portal.password</code> définissent la connexion du pool principal.</p><p>Dans un site construit avec Maven, ce fichier est surchargé via un <em>overlay</em> du site, ce qui évite de modifier les fichiers livrés par les plugins.</p>',2,1,'2025-09-02 09:30:00',3);

-- Sujet 3 : Premiers pas
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (5,'Comment accéder au back office pour la première fois ?','<p>Le back office est accessible à l''adresse <code>/jsp/admin/AdminLogin.jsp</code> de votre site. À la première connexion, utilisez le compte administrateur créé par le script d''initialisation, puis changez immédiatement son mot de passe depuis la rubrique <em>Gestion des utilisateurs</em>.</p>',3,1,'2025-09-03 10:00:00',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (6,'Quelle est la différence entre une page, une rubrique et une application XPage ?','<p>Une <strong>page</strong> est un nœud de l''arborescence du site, avec son titre, sa description et son modèle de présentation.</p><p>Une <strong>rubrique</strong> (ou portlet) est un bloc de contenu placé dans une colonne d''une page : liste d''articles, HTML libre, menu, etc.</p><p>Une <strong>XPage</strong> est une application dynamique fournie par un plugin (formulaires, rendez-vous, FAQ...) et servie via <code>Portal.jsp?page=nomDuPlugin</code>.</p>',3,1,'2025-09-03 10:05:00',1);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (7,'Je ne vois pas mes modifications en front office, pourquoi ?','<p>Lutece met en cache les pages et de nombreux services. Après une modification de contenu ou de configuration, videz les caches depuis <em>Système &gt; Gestion des caches</em> dans le back office.</p><p>Vérifiez aussi que la page ou la rubrique est bien publiée et que ses dates de validité sont correctes.</p>',3,1,'2025-09-03 10:10:00',2);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (8,'Où trouver les normes de documentation du wiki ?','<p>La rubrique <em>Premiers pas</em> du wiki contient une page <em>Normes de documentation</em> qui décrit le plan attendu pour chaque page (aperçu général, principes d''intégration, procédures pas à pas) et les conventions de rédaction.</p>',3,1,'2025-09-03 10:15:00',3);

-- Sujet 5 : Plugin Forms
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (9,'Comment créer un formulaire avec le plugin Forms ?','<p>Depuis le back office, ouvrez <em>Gestion des formulaires</em> puis <em>Créer un formulaire</em>. Renseignez le titre, la description et les dates de disponibilité.</p><p>Ajoutez ensuite au moins une <strong>étape</strong>, puis des <strong>groupes</strong> et des <strong>questions</strong> dans chaque étape. Enfin, publiez le formulaire pour le rendre accessible en front office.</p>',5,1,'2025-09-10 14:00:00',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (10,'Quels types d''entrée sont disponibles pour les questions ?','<p>Le plugin Forms propose notamment : texte court, texte long, liste déroulante, case à cocher, bouton radio, date, numérique, téléphone, fichier, image, géolocalisation, commentaire (texte non saisissable), tableau, session utilisateur et champ caché.</p><p>Chaque type dispose de ses propres options (obligatoire, valeur par défaut, expression régulière, taille maximale...). La page <em>Types d''entrée (Forms)</em> du wiki les détaille un par un.</p>',5,1,'2025-09-10 14:05:00',1);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (11,'Comment exporter les réponses d''un formulaire en PDF ?','<p>Deux possibilités :</p><ul><li>l''export manuel depuis la liste des réponses, via le module <em>Forms to PDF</em>, qui génère un PDF par réponse à partir d''un modèle ;</li><li>la génération automatique par le <em>module Workflow-FormsToPDF</em>, en ajoutant une tâche dédiée à une action du workflow associé au formulaire.</li></ul><p>Le modèle PDF est un gabarit dans lequel les questions sont référencées par leur code.</p>',5,1,'2025-09-10 14:10:00',2);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (12,'Peut-on afficher les résultats d''un sondage sous forme de graphiques ?','<p>Oui, avec le <em>module Forms Poll</em>. Il ajoute une vue front office qui présente, pour les questions à choix (liste, cases à cocher, radio), la répartition des réponses sous forme de graphiques.</p><p>L''affichage se configure formulaire par formulaire, et peut être limité aux utilisateurs ayant eux-mêmes répondu.</p>',5,1,'2025-09-10 14:15:00',3);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (13,'Comment conditionner l''affichage d''une question à une réponse précédente ?','<p>Dans l''édition d''une étape, ajoutez une <strong>transition</strong> conditionnelle entre deux étapes, ou utilisez les <strong>contrôles</strong> d''affichage sur un groupe de questions. Le contrôle compare la réponse d''une question de référence à une valeur attendue et affiche ou masque le groupe en conséquence.</p>',5,1,'2025-09-10 14:20:00',4);

-- Sujet 6 : Plugin Appointment
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (14,'Comment définir les créneaux de rendez-vous d''un formulaire ?','<p>Dans le back office de l''application Rendez-vous, chaque formulaire possède une ou plusieurs <strong>définitions de semaine</strong> : jours ouverts, heures d''ouverture et de fermeture, durée des créneaux et capacité (nombre de places par créneau).</p><p>Une définition de semaine s''applique à partir d''une date donnée, ce qui permet de faire évoluer les horaires sans perdre l''historique.</p>',6,1,'2025-09-12 11:00:00',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (15,'Comment fermer exceptionnellement une journée ou un créneau ?','<p>Utilisez les <strong>jours de fermeture</strong> pour bloquer une date entière (jour férié, fermeture du service). Pour un créneau précis, ouvrez le calendrier du formulaire, sélectionnez le créneau et passez-le en <em>fermé</em> : il n''est alors plus proposé aux usagers, sans impact sur les rendez-vous déjà pris.</p>',6,1,'2025-09-12 11:05:00',1);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (16,'Qu''est-ce que la prise de rendez-vous connectée à l''ANTS ?','<p>Pour les démarches de titres d''identité (CNI, passeport), le plugin peut se connecter à l''API de l''Agence Nationale des Titres Sécurisés. L''usager saisit son numéro de pré-demande ANTS ; le plugin vérifie sa validité et remonte le rendez-vous dans le système national afin d''éviter les doublons entre communes.</p><p>La configuration (URL de l''API, clé, formulaires concernés) est décrite dans la page <em>Prise de rendez-vous connectée à l''ANTS</em> du wiki.</p>',6,1,'2025-09-12 11:10:00',2);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (17,'Quels droits faut-il pour gérer un formulaire de rendez-vous ?','<p>L''accès au module passe par le droit <em>Gestion des rendez-vous</em>. Les actions fines (modifier le formulaire, gérer les créneaux, consulter ou annuler les rendez-vous, exporter) sont contrôlées par des permissions RBAC portées sur chaque formulaire, à attribuer aux rôles d''administration.</p><p>Voir la page <em>Droits et Rôles du plugin Appointment</em>.</p>',6,1,'2025-09-12 11:15:00',3);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (18,'Comment l''usager reçoit-il la confirmation de son rendez-vous ?','<p>Les notifications (confirmation, rappel, annulation) sont gérées par le <strong>workflow</strong> associé au formulaire de rendez-vous, grâce aux tâches de notification par e-mail ou SMS. Le contenu des messages est paramétrable et peut inclure la date, l''heure, le lieu et un lien d''annulation.</p>',6,1,'2025-09-12 11:20:00',4);

-- Sujet 7 : Workflow
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (19,'Quels sont les concepts de base d''un workflow Lutece ?','<p>Un workflow est composé d''<strong>états</strong>, d''<strong>actions</strong> qui font passer une ressource d''un état à un autre, et de <strong>tâches</strong> exécutées lors d''une action (notification, changement de valeur, génération de document, appel de service...).</p><p>Les ressources gérées peuvent être des réponses de formulaires, des rendez-vous, des articles de blog, etc.</p>',7,1,'2025-09-15 09:00:00',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (20,'Comment associer un workflow à un formulaire ?','<p>Créez d''abord le workflow dans <em>Gestion des workflows</em>, avec au moins un état initial. Puis, dans les paramètres du formulaire (Forms ou Rendez-vous), sélectionnez ce workflow dans la liste déroulante prévue. Chaque nouvelle réponse ou nouveau rendez-vous entre alors automatiquement dans l''état initial.</p>',7,1,'2025-09-15 09:05:00',1);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (21,'Peut-on déclencher une action automatiquement ?','<p>Oui. Une action peut être marquée <em>automatique</em> : elle est exécutée dès l''entrée dans l''état qui la porte. On peut aussi utiliser des <strong>actions réflexives</strong> et des tâches de type <em>changement d''état différé</em> pour programmer des relances.</p>',7,1,'2025-09-15 09:10:00',2);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (22,'Le workflow des rendez-vous a-t-il des tâches spécifiques ?','<p>Oui. Le module Workflow-Appointment ajoute des tâches dédiées : notification à l''usager et à l''agent, mise à jour du statut du rendez-vous, gestion des places, ou encore création d''un rendez-vous de suivi. Elles sont décrites dans la page <em>Workflow spécifique à Rendez-vous</em>.</p>',7,1,'2025-09-15 09:15:00',3);

-- Sujet 8 : Plugin Blog
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (23,'À quoi sert le plugin Blog ?','<p>Le plugin Blog permet de rédiger et publier des contenus éditoriaux riches (articles, actualités, pages HTML) avec un éditeur WYSIWYG, des images, des étiquettes (tags) et un historique des versions.</p><p>Les contenus sont ensuite affichés dans des rubriques de type <em>Blog</em> placées sur les pages du site.</p>',8,1,'2025-09-18 16:00:00',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (24,'Quelle différence entre Plugin Blog et Plugin HTMLPage ?','<p>Le plugin <strong>Blog</strong> est orienté articles : versions, tags, dates de publication, portlets de liste.</p><p>Le plugin <strong>HTMLPage</strong> sert à créer des fragments HTML simples réutilisables (bandeaux, mentions, encarts), sans cycle de publication éditorial.</p>',8,1,'2025-09-18 16:05:00',1);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (25,'Comment programmer la publication d''un article ?','<p>Lors de la publication d''un article dans une rubrique Blog, renseignez la <em>date de début</em> et éventuellement la <em>date de fin</em> de publication. L''article apparaît et disparaît automatiquement selon ces dates, sans intervention manuelle.</p>',8,1,'2025-09-18 16:10:00',2);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (26,'Puis-je restaurer une ancienne version d''un article ?','<p>Oui. Chaque enregistrement crée une nouvelle version. Depuis la fiche de l''article, l''onglet <em>Versions</em> liste l''historique et permet de consulter ou de restaurer une version antérieure.</p>',8,0,'2025-09-18 16:15:00',3);

-- Sujet 9 : Autres plugins
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (27,'Comment mettre en place un moteur de recherche SolR avec une carte Leaflet ?','<p>Le plugin <strong>SolR</strong> indexe les contenus du site dans un serveur Apache Solr et fournit une page de recherche à facettes. Couplé au plugin <strong>Leaflet</strong>, les résultats géolocalisés peuvent être affichés sur une carte interactive.</p><p>La configuration comprend l''URL du serveur Solr, les indexeurs à activer et les champs de facettes.</p>',9,1,'2025-09-22 10:00:00',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (28,'Comment intégrer les statistiques Matomo ?','<p>Le plugin <strong>Matomo</strong> ajoute le script de suivi sur toutes les pages du site. Renseignez l''URL du serveur Matomo et l''identifiant du site dans les propriétés du plugin. Un tableau de bord back office permet ensuite de consulter les principales statistiques de fréquentation.</p>',9,1,'2025-09-22 10:05:00',1);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (29,'À quoi servent les plugins Unittree et MyDashboard ?','<p><strong>Unittree</strong> organise les agents en unités hiérarchiques (directions, services) et permet d''affecter des ressources, par exemple des demandes, à une unité.</p><p><strong>MyDashboard</strong> propose un tableau de bord personnalisable pour les usagers connectés : chaque composant (mes demandes, mes rendez-vous...) est fourni par un plugin et peut être activé ou ordonné par l''usager.</p>',9,1,'2025-09-22 10:10:00',2);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (30,'Comment afficher des données cartographiques sur mon site ?','<p>Plusieurs plugins couvrent ce besoin : <strong>Leaflet</strong> pour afficher une carte et des points, <strong>GISMAP</strong> pour des couches SIG plus riches, et <strong>Carto</strong> pour une cartographie complète directement dans Lutece. Le choix dépend de la source des données et du niveau d''interactivité souhaité.</p>',9,1,'2025-09-22 10:15:00',3);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (31,'Existe-t-il un outil de démocratie participative ?','<p>Oui, le plugin <strong>Suggest</strong> permet de recueillir des propositions d''usagers, de les faire commenter et voter, avec une modération en back office. Il est utilisé pour les budgets participatifs et les consultations.</p>',9,1,'2025-09-22 10:20:00',4);

-- Sujet 11 : Arborescence et pages
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (32,'Comment créer l''arborescence des pages du site ?','<p>Dans <em>Administration du site</em>, sélectionnez la page parente puis <em>Créer une page enfant</em>. Renseignez le nom, la description, le modèle de présentation et l''ordre d''affichage. Répétez l''opération pour construire l''arborescence complète.</p><p>L''ordre des pages sœurs se modifie ensuite via les flèches de la liste des pages enfants.</p>',11,1,'2025-10-01 09:00:00',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (33,'Comment changer le modèle de présentation d''une page ?','<p>Ouvrez les propriétés de la page et choisissez un autre <em>modèle de page</em> (une colonne, deux colonnes, etc.). Attention : si des rubriques sont placées dans une colonne qui n''existe pas dans le nouveau modèle, elles ne seront plus affichées jusqu''à leur déplacement.</p>',11,1,'2025-10-01 09:05:00',1);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (34,'Comment restreindre l''accès à une page à certains usagers ?','<p>Dans les propriétés de la page, associez un <strong>rôle</strong> front office. Seuls les usagers authentifiés (via MyLutece) disposant de ce rôle verront la page et son contenu dans le menu.</p>',11,1,'2025-10-01 09:10:00',2);

-- Sujet 12 : Propriétés du site
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (35,'Qu''est-ce qu''une propriété du site ?','<p>Les propriétés du site sont des paramètres modifiables depuis le back office (<em>Système &gt; Propriétés du site</em>) sans redéploiement : nom du site, adresse e-mail de contact, textes de pied de page, options d''affichage...</p><p>Elles sont stockées dans le datastore sous des clés de la forme <code>portal.site.site_property.xxx</code>.</p>',12,1,'2025-10-03 14:00:00',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (36,'Comment créer un groupe de propriétés du site ?','<p>Un groupe rassemble des propriétés liées dans un même onglet. Il se déclare dans le fichier de configuration du plugin ou du site, puis les propriétés sont créées dans le datastore avec le préfixe du groupe. La page <em>Créer un groupe de propriétés du site</em> du wiki détaille la procédure pas à pas.</p>',12,1,'2025-10-03 14:05:00',1);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (37,'Comment changer le thème graphique du site ?','<p>Les thèmes sont fournis par des plugins <em>site-theme</em>. Une fois le thème déployé, sélectionnez-le dans <em>Système &gt; Gestion des thèmes</em>. Les habillages (logo, couleurs, pied de page) se personnalisent ensuite via les propriétés du site.</p>',12,1,'2025-10-03 14:10:00',2);

-- Sujet 13 : Droits, rôles et groupes de travail
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (38,'Quelle différence entre un droit, un rôle et un groupe de travail ?','<p>Un <strong>droit</strong> donne accès à une fonctionnalité du back office (ex. Gestion des formulaires).</p><p>Un <strong>rôle</strong> d''administration regroupe des permissions RBAC fines sur des ressources (ex. modifier tel formulaire).</p><p>Un <strong>groupe de travail</strong> filtre les ressources visibles : un administrateur ne voit que les ressources rattachées à ses groupes.</p>',13,1,'2025-10-06 11:00:00',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (39,'Comment restreindre l''édition de documents à certains gestionnaires ?','<p>Rattachez les documents (ou les formulaires, pages, articles) à un <strong>groupe de travail</strong> et n''affectez ce groupe qu''aux gestionnaires concernés. Complétez si besoin par un rôle RBAC limitant les actions (lecture seule, modification, publication).</p><p>La page <em>Restreindre l''édition de documents à certains gestionnaires</em> détaille un exemple complet.</p>',13,1,'2025-10-06 11:05:00',1);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (40,'Comment créer un compte administrateur avec des droits limités ?','<p>Dans <em>Gestion des utilisateurs</em>, créez l''utilisateur avec le niveau adéquat, puis affectez-lui uniquement les droits nécessaires, les rôles RBAC correspondants et ses groupes de travail. Évitez le niveau 0 (super-administrateur) pour les comptes courants.</p>',13,1,'2025-10-06 11:10:00',2);

-- Sujet 15 : Changelogs
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (41,'Où consulter les nouveautés de chaque version ?','<p>La rubrique <em>Références &gt; Changelogs</em> du wiki liste, pour chaque version du cœur et des principaux plugins, les nouveautés, corrections et éventuelles actions de migration (scripts SQL de mise à jour).</p>',15,1,'2025-10-08 10:00:00',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (42,'Comment savoir quelle version d''un plugin est installée ?','<p>Dans le back office, <em>Système &gt; Gestion des plugins</em> affiche la liste des plugins déployés avec leur version, leur état (activé ou non) et leur pool de base de données.</p>',15,1,'2025-10-08 10:05:00',1);

-- Sujet 16 : Guides utilisateurs
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (43,'Existe-t-il des guides utilisateurs téléchargeables ?','<p>Oui. La rubrique <em>Références &gt; Guides utilisateurs</em> regroupe des guides pas à pas destinés aux agents : prise de rendez-vous, gestion des formulaires, publication d''articles. Ils complètent les pages du wiki par des captures d''écran.</p>',16,1,'2025-10-08 10:10:00',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (44,'Comment tester mes connaissances sur une rubrique ?','<p>Chaque rubrique du wiki propose un lien <em>Test your knowledge</em> qui ouvre un quiz de quelques questions sur le contenu de la rubrique. C''est un bon moyen de valider une prise en main avant une mise en production.</p>',16,1,'2025-10-08 10:15:00',1);

-- Sujet 18 : POM de site et build
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (45,'Qu''est-ce qu''un POM de site ?','<p>Un site Lutece est un projet Maven de packaging <code>lutece-site</code>. Son <code>pom.xml</code> déclare le cœur (<code>lutece-core</code>) et les plugins à embarquer en dépendances, ainsi que les surcharges de configuration (<em>overlays</em>) propres au site.</p><p>La commande <code>mvn lutece:site-assembly</code> produit le WAR déployable.</p>',18,1,'2025-10-13 09:00:00',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (46,'Comment ajouter un plugin à mon site ?','<p>Ajoutez la dépendance du plugin dans le POM du site, en vérifiant que sa version est compatible avec la version du cœur utilisée. Reconstruisez le WAR, redéployez, puis activez le plugin dans <em>Système &gt; Gestion des plugins</em> et exécutez ses scripts SQL si nécessaire (ou laissez Liquibase le faire en Lutece 8).</p>',18,1,'2025-10-13 09:05:00',1);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (47,'Comment surcharger un fichier de configuration ou un template ?','<p>Placez le fichier à surcharger dans le répertoire <code>webapp/</code> du site, au même chemin que dans le plugin d''origine (par exemple <code>webapp/WEB-INF/conf/plugins/helpdesk.properties</code>). Lors de l''assemblage, l''overlay du site prend le dessus sur le fichier livré par le plugin.</p>',18,1,'2025-10-13 09:10:00',2);

-- Sujet 19 : Migration vers Lutece 8
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (48,'Quels sont les principaux changements de Lutece 8 pour un développeur ?','<p>Lutece 8 repose sur Jakarta EE 10 : passage des packages <code>javax.*</code> à <code>jakarta.*</code>, remplacement de Spring par <strong>CDI</strong> pour l''injection, configuration des beans en JSON plutôt qu''en XML, et gestion des scripts SQL par <strong>Liquibase</strong>.</p><p>Les templates back office utilisent le thème Tabler et les macros FreeMarker BO, les templates front office les macros FO du cœur.</p>',19,1,'2025-10-15 15:00:00',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (49,'Sur quelle branche Git se trouve la version 8 d''un plugin ?','<p>Par convention, la branche <code>develop</code> porte la version 8. Les versions 7 sont maintenues sur les branches <code>develop_core7</code> et <code>master_core7</code>. Vérifiez la version du parent dans le <code>pom.xml</code> : elle doit commencer par <code>8.</code>.</p>',19,1,'2025-10-15 15:05:00',1);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (50,'Mes scripts SQL ne s''exécutent plus à l''installation, pourquoi ?','<p>En Lutece 8, les scripts des répertoires <code>src/sql/plugins/xxx/plugin</code> et <code>core</code> doivent porter l''en-tête Liquibase (<code>-- liquibase formatted sql</code> et <code>-- changeset</code>). Sans cet en-tête, ils sont ignorés. Vérifiez aussi que le nom du changeset est unique.</p>',19,1,'2025-10-15 15:10:00',2);

-- Sujet 20 : Exploitation
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (51,'Où sont les journaux applicatifs ?','<p>Les journaux sont écrits par Log4j2 dans le répertoire <code>WEB-INF/logs</code> du site déployé (ou dans le répertoire configuré par l''exploitant). Le niveau de log se règle dans <code>WEB-INF/conf/log.properties</code> ou via la surcharge du site.</p>',20,1,'2025-10-20 08:30:00',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (52,'Comment surveiller et relancer les démons ?','<p>La rubrique <em>Système &gt; Gestion des démons</em> du back office liste les tâches planifiées (indexation, envoi des mails, purge...), leur dernier passage et leur résultat. Chaque démon peut y être activé, désactivé ou lancé immédiatement.</p>',20,1,'2025-10-20 08:35:00',1);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES
 (53,'Peut-on déployer Lutece sur plusieurs instances ?','<p>Oui, à condition de partager la base de données et d''externaliser ou de synchroniser les caches (par exemple avec Hazelcast) et les fichiers téléversés. Les sessions doivent être répliquées ou l''affinité de session activée sur le répartiteur de charge.</p>',20,0,'2025-10-20 08:40:00',2);

--
-- helpdesk_visitor_question (questions posées via le formulaire de contact)
--

-- Questions archivées (répondues par l'administrateur 1)
INSERT INTO helpdesk_visitor_question (id_visitor_question,last_name,first_name,email,question,answer,date_visitor_question,id_user,id_theme) VALUES
 (1,'Martin','Sophie','sophie.martin@example.org','Bonjour, je n''arrive pas à me connecter au back office après l''installation avec Docker. Quel est le mot de passe par défaut ?','Bonjour, les identifiants de démonstration sont indiqués sur la page Images docker de démonstration du wiki. Pensez à les changer dès la première connexion. Cordialement.','2026-01-12',1,1);
INSERT INTO helpdesk_visitor_question (id_visitor_question,last_name,first_name,email,question,answer,date_visitor_question,id_user,id_theme) VALUES
 (2,'Durand','Karim','karim.durand@example.org','Est-il possible de limiter le nombre de réponses à un formulaire par usager ?','Oui, dans les paramètres du formulaire, activez l''option limitant à une réponse par utilisateur connecté. Pour les usagers anonymes, la limitation n''est pas possible de façon fiable.','2026-02-03',1,3);
INSERT INTO helpdesk_visitor_question (id_visitor_question,last_name,first_name,email,question,answer,date_visitor_question,id_user,id_theme) VALUES
 (3,'Lefebvre','Claire','claire.lefebvre@example.org','Nos agents ne reçoivent plus les notifications de nouveaux rendez-vous depuis hier.','Bonjour, vérifiez dans Gestion des démons que le démon d''envoi des mails est actif et consultez son dernier résultat. Si la file est bloquée, relancez-le manuellement.','2026-02-17',1,4);
INSERT INTO helpdesk_visitor_question (id_visitor_question,last_name,first_name,email,question,answer,date_visitor_question,id_user,id_theme) VALUES
 (4,'Nguyen','Thomas','thomas.nguyen@example.org','Comment faire pour qu''un article de blog ne soit visible que par les usagers connectés ?','Associez un rôle front office à la page qui porte la rubrique Blog. Seuls les usagers authentifiés disposant de ce rôle verront la page.','2026-03-05',1,6);

-- Questions en attente de réponse
INSERT INTO helpdesk_visitor_question (id_visitor_question,last_name,first_name,email,question,answer,date_visitor_question,id_user,id_theme) VALUES
 (5,'Bernard','Julie','julie.bernard@example.org','Peut-on exporter les réponses d''un formulaire au format CSV en plus du PDF ?','','2026-04-08',0,3);
INSERT INTO helpdesk_visitor_question (id_visitor_question,last_name,first_name,email,question,answer,date_visitor_question,id_user,id_theme) VALUES
 (6,'Moreau','Antoine','antoine.moreau@example.org','Comment ajouter une étape de validation par un responsable dans le workflow des demandes ?','','2026-04-21',0,5);
INSERT INTO helpdesk_visitor_question (id_visitor_question,last_name,first_name,email,question,answer,date_visitor_question,id_user,id_theme) VALUES
 (7,'Petit','Isabelle','isabelle.petit@example.org','Le changement de thème graphique ne s''applique pas sur toutes les pages, certaines gardent l''ancien habillage.','','2026-05-06',0,7);
INSERT INTO helpdesk_visitor_question (id_visitor_question,last_name,first_name,email,question,answer,date_visitor_question,id_user,id_theme) VALUES
 (8,'Roux','Mehdi','mehdi.roux@example.org','Où trouver la liste complète des plugins compatibles Lutece 8 ?','','2026-05-19',0,9);
INSERT INTO helpdesk_visitor_question (id_visitor_question,last_name,first_name,email,question,answer,date_visitor_question,id_user,id_theme) VALUES
 (9,'Garcia','Elena','elena.garcia@example.org','Nous voulons passer en cluster sur deux serveurs : quels sont les prérequis côté cache ?','','2026-06-02',0,10);
INSERT INTO helpdesk_visitor_question (id_visitor_question,last_name,first_name,email,question,answer,date_visitor_question,id_user,id_theme) VALUES
 (10,'Fournier','Lucas','','Je ne trouve pas où modifier le texte du pied de page du site.','','2026-06-15',0,8);

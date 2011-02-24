--
-- Dumping data for table helpdesk_faq
--
INSERT INTO helpdesk_faq (id_faq,name,description,role_key,workgroup_key) VALUES (1,'F.A.Q. Lutèce','F.A.Q. Lutèce','none','all');

--
-- Dumping data for table helpdesk_subject
--
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (0,'',0,0);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (1,'Administation du Site',0,0);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (2,'Publication',0,1);
INSERT INTO helpdesk_subject (id_subject,subject,id_parent,id_order) VALUES (3,'Documents',2,0);

--
-- Dumping data for table helpdesk_theme
--
INSERT INTO helpdesk_theme (id_theme,theme,id_mailing_list,id_parent,id_order) VALUES (0,'','1',0,0);
INSERT INTO helpdesk_theme (id_theme,theme,id_mailing_list,id_parent,id_order) VALUES (1,'Administration du Site','1',0,0);
INSERT INTO helpdesk_theme (id_theme,theme,id_mailing_list,id_parent,id_order) VALUES (2,'Publication','1',0,1);
INSERT INTO helpdesk_theme (id_theme,theme,id_mailing_list,id_parent,id_order) VALUES (3,'Documents','1',2,0);


--
-- Dumping data for table helpdesk_ln_faq_subject
--
INSERT INTO helpdesk_ln_faq_subject (id_faq,id_subject) VALUES (1,1);
INSERT INTO helpdesk_ln_faq_subject (id_faq,id_subject) VALUES (1,2);


--
-- Dumping data for table helpdesk_ln_faq_theme
--
INSERT INTO helpdesk_ln_faq_theme (id_faq,id_theme) VALUES (1,1);
INSERT INTO helpdesk_ln_faq_theme (id_faq,id_theme) VALUES (1,2);


--
-- Dumping data for table helpdesk_question_answer
--
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES 
 (1,'Je viens de créer une rubrique dans la page courante et elle n''apparaît pas dans l''aperçu de prévisualisation : pourquoi ?','Il peut s''agir du modèle de présentation sélectionné pour votre page : si vous créez un rubrique que vous placez en 2e colonne, assurez-vous que le modèle associé à la page en cours comprend deux colonnes (par défaut, la composition à une colonne est sélectionnée).\r\n<br /> Un autre cas peut se présenter : vous avez utilisé le plan du site pour accéder à une page et vous utilisez ensuite le formulaire d''administration pour créer une rubrique dans la page. Vérifier en haut du formulaire d''administration du site que la page courante est bien celle affichée en prévisualisation. Dans le cas contraire, la rubrique que vous venez de créer a été placée dans la page indiquée comme page courante.',1,1,'2008-10-07 14:47:05',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES 
 (2,'Pourquoi sommes-nous obligés de saisir un titre de rubrique au moment de sa création lorsque l''on choisit ensuite un style \"Sans titre\" ?','Les contrôles effectués sur les zones de saisie au moment de la validation du formulaire de création d''une rubrique sont les mêmes, quelque soit le choix de la mise en forme retenu. Comme les styles correspondent à des mises en forme spéciales du contenu, nous n''avons pas les moyens de savoir, au niveau du formulaire, si la mise en forme choisie affiche ou non le titre.\r\n<br /> Dans une prochaine version de l''outil, il sera possible de créer des alias de rubrique, c''est-à-dire de placer dans une autre page, le contenu d''une rubrique déjà créée, sans avoir à resaisir son contenu. Le choix de la rubrique à dupliquer sera fera à partir de son titre (qu''il faudra avoir renseigné).',1,1,'2008-10-07 14:47:25',1);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES 
 (3,'L''article que je viens de publier n''apparait pas en prévisualisation dans la rubrique sélectionnée : pourquoi ?','La publication consiste à créer un lien entre un article produit par un fournisseur et une rubrique de type \"&nbsp;Liste d''articles&nbsp;\". Pour s''assurer que la validité d''un article publié sur le portail ne soit pas dépassée, on définit au niveau de chaque article la période pendant laquelle l''article sera visible sur le portail (date de début de validité et date de fin de validité). Dès que la date de fin de validité est atteinte, l''article n''apparaît plus dans la liste.\r\n<br /> Si vous publiez un article dont la date de début de validité n''est pas encore atteinte ou dont la date de fin de validité est dépassée, l''article n''apparaîtra pas dans la rubrique !',3,1,'2008-10-07 14:47:48',0);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES 
 (4,'L''article que je viens de créer n''apparait pas dans la liste des articles à publier : pourquoi ?','Dans le cas de l''utilisation d''une base partagée et d''un ensemble de bases locales (comme c''est le cas pour les portails des mairies d''arrondissement), la création d''un article se fait dans la base partagée, même si la portée du flux utilisé pour sa création est locale à votre portail.\r\n<br /> Pour pouvoir publier votre article dans les rubriques de votre portail, vous devez synchroniser votre base avec la base partagée. Cette opération consiste à rapatrier dans votre base locale les articles créés en base partagée.',3,1,'2008-10-07 14:48:03',1);
INSERT INTO helpdesk_question_answer (id_question_answer,question,answer,id_subject,status,creation_date,id_order) VALUES 
 (5,'A quoi sert l''auto-publication ?','L''auto-publication a été mise en place pour permettre à certains fournisseurs de contenu de voir leurs articles publiés directement dans une rubrique du portail, sans que le webmestre intervienne.\r\n<br /> <em>Exemple :</em> Le fournisseur peut être une association locale, pour laquelle un flux local d''articles aura été créé sur le portail, et un compte utilisateur aura été mis à disposition de son responsable. Comme l''auto-publication permet d''associer un flux local à une rubrique de type liste d''articles, les articles produits par le responsable de l''association seront directement publiés dans sa rubrique.',3,1,'2008-10-07 14:48:28',2);

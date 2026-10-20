package com.sourate.app

object IslamicRepository {

    fun getCategories(): List<Category> = listOf(
        piliersDeLislam(),
        articlesDeLaFoi(),
        lesPrieres(),
        apprendreLaPriere(),
        invocations(),
        souratesCourtes(),
        rappelsEtBienfaits()
    )

    private fun apprendreLaPriere() = Category(
        id = 7,
        title = "Apprentissage",
        subtitle = "J'apprends : les ablutions et la prière",
        iconRes = R.drawable.ic_prayer,
        sections = listOf(
            Section(heading="Document complet — 12 pages", items=listOf(
                ContentItem(title="Page 1", description="", assetImage="pages/page_01.png"),
                ContentItem(title="Page 2", description="", assetImage="pages/page_02.png"),
                ContentItem(title="Page 3", description="", assetImage="pages/page_03.png"),
                ContentItem(title="Page 4", description="", assetImage="pages/page_04.png"),
                ContentItem(title="Page 5", description="", assetImage="pages/page_05.png"),
                ContentItem(title="Page 6", description="", assetImage="pages/page_06.png"),
                ContentItem(title="Page 7", description="", assetImage="pages/page_07.png"),
                ContentItem(title="Page 8", description="", assetImage="pages/page_08.png"),
                ContentItem(title="Page 9", description="", assetImage="pages/page_09.png"),
                ContentItem(title="Page 10", description="", assetImage="pages/page_10.png"),
                ContentItem(title="Page 11", description="", assetImage="pages/page_11.png"),
                ContentItem(title="Page 12", description="", assetImage="pages/page_12.png")
            ))
        )
    )
    private fun piliersDeLislam() = Category(
        id = 1,
        title = "Les 5 Piliers de l'Islam",
        subtitle = "Les fondements de la religion musulmane",
        iconRes = R.drawable.ic_pillars,
        sections = listOf(
            Section(
                heading = "Premier Pilier : La Chahada",
                items = listOf(
                    ContentItem(
                        title = "Le Témoignage de Foi",
                        arabic = "أَشْهَدُ أَنْ لَا إِلَٰهَ إِلَّا ٱللَّٰهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا رَسُولُ ٱللَّٰه",
                        description = "Attester qu'il n'y a de divinité digne d'adoration qu'Allah et que Muhammad ﷺ est Son serviteur et Messager. C'est la porte d'entrée en Islam. Par ce témoignage, le musulman reconnaît l'unicité divine (Tawhid) et accepte la mission prophétique de Muhammad ﷺ."
                    )
                )
            ),
            Section(
                heading = "Deuxième Pilier : La Salat (Prière)",
                items = listOf(
                    ContentItem(
                        title = "Les 5 prières quotidiennes",
                        description = "Prier Allah cinq fois par jour : Fajr, Dhouhr, Asr, Maghrib et Isha. La prière est le lien direct entre le serviteur et son Créateur. Elle purifie l'âme, rappelle le sens de la vie et empêche du mal. Le Prophète ﷺ a dit : « La première chose pour laquelle le serviteur sera jugé au Jour du Jugement est la prière. »"
                    )
                )
            ),
            Section(
                heading = "Troisième Pilier : La Zakat (Aumône)",
                items = listOf(
                    ContentItem(
                        title = "L'aumône légale",
                        description = "Donner une part de sa richesse aux nécessiteux, soit 2,5% de l'épargne atteignant le Nisab (seuil minimal). La Zakat purifie la fortune et le cœur, et partage la bénédiction dans la société. Elle est obligatoire pour tout musulman possédant les conditions requises."
                    )
                )
            ),
            Section(
                heading = "Quatrième Pilier : Le Sawm (Jeûne)",
                items = listOf(
                    ContentItem(
                        title = "Le jeûne du mois de Ramadan",
                        description = "S'abstenir de manger, boire et relations intimes du lever au coucher du soleil pendant tout le mois de Ramadan. Le jeûne enseigne la patience, la discipline et la compassion envers les plus démunis. Allah a dit : « Je suis le proche du jeûneur. Je suis la récompense de son jeûne. »"
                    )
                )
            ),
            Section(
                heading = "Cinquième Pilier : Le Hajj (Pèlerinage)",
                items = listOf(
                    ContentItem(
                        title = "Le pèlerinage à La Mecque",
                        arabic = "وَلِلَّٰهِ عَلَى ٱلنَّاسِ حِجُّ ٱلْبَيْتِ مَنِ ٱسْتَطَاعَ إِلَيْهِ سَبِيلًا",
                        description = "Le Hajj est obligatoire une fois dans la vie pour tout musulman en capacité physique et financière. C'est un rassemblement mondial de millions de croyants autour de la Kaaba, répétant les gestes du Prophète Ibrahim ﷺ. Le Hajj efface les péchés antérieurs et rapproche d'Allah."
                    )
                )
            )
        )
    )

    private fun articlesDeLaFoi() = Category(
        id = 2,
        title = "Les 6 Articles de la Foi",
        subtitle = "Les croyances fondamentales en Islam",
        iconRes = R.drawable.ic_faith,
        sections = listOf(
            Section(
                heading = "1. La Croyance en Allah",
                items = listOf(
                    ContentItem(
                        title = "L'unicité divine (Tawhid)",
                        arabic = "قُلْ هُوَ ٱللَّٰهُ أَحَدٌ ۝ ٱللَّٰهُ ٱلصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُۥ كُفُوًا أَحَدٌ",
                        description = "Croire fermement qu'Allah est le seul Dieu, sans associé, sans début ni fin, Créateur et Souverain de tout l'Univers. Il est unique dans Ses attributs et Ses actes. Sourate Al-Ikhlas (112) résume parfaitement cette croyance."
                    )
                )
            ),
            Section(
                heading = "2. La Croyance aux Anges",
                items = listOf(
                    ContentItem(
                        title = "Les créations lumineuses",
                        description = "Croire aux anges, créations d'Allah faites de lumière, obéissantes et sans volonté propre. Gabriel (Jibril) a transmis la révélation à Muhammad ﷺ. Mikael est chargé de la pluie et des provisions. Israfil sonnera de la trompette le Jour du Jugement. À chaque musulman est assigné un ange gardien."
                    )
                )
            ),
            Section(
                heading = "3. La Croyance aux Livres Saints",
                items = listOf(
                    ContentItem(
                        title = "Les révélations divines",
                        description = "Croire aux livres révélés par Allah : le Tourat (Torah) de Moïse (Moussa), le Zabour (Psaumes) de David (Dawoud), l'Injil (Évangile) de Jésus (Issa) et le Coran de Muhammad ﷺ. Le Coran est la dernière révélation, non altérée, confirmant les messages précédents."
                    )
                )
            ),
            Section(
                heading = "4. La Croyance aux Prophètes",
                items = listOf(
                    ContentItem(
                        title = "Les messagers d'Allah",
                        description = "Croire aux 25 prophètes mentionnés dans le Coran, depuis Adam ﷺ jusqu'à Muhammad ﷺ. Tous ont transmis le même message : l'adoration exclusive d'Allah. Parmi les plus connus : Nuh (Noé), Ibrahim (Abraham), Moussa (Moïse) et Issa (Jésus) ﷺ. Le Prophète Muhammad ﷺ est le dernier des prophètes."
                    )
                )
            ),
            Section(
                heading = "5. La Croyance au Jour du Jugement",
                items = listOf(
                    ContentItem(
                        title = "Le Jour de la Résurrection",
                        description = "Croire à la fin de ce monde, à la résurrection des morts, au Jugement et à l'entrée au Paradis ou en Enfer selon les œuvres de chacun. C'est un jour de justice absolue où chaque personne rendra compte de ses actes."
                    )
                )
            ),
            Section(
                heading = "6. La Croyance au Qadar",
                items = listOf(
                    ContentItem(
                        title = "Le Décret divin",
                        description = "Croire que tout ce qui arrive — le bien et le mal — se fait par la volonté et la sagesse d'Allah. Cela n'enlève pas la responsabilité humaine : chacun est libre de ses choix et en sera jugé. Le croyant accepte la divine sagesse tout en agissant avec effort."
                    )
                )
            )
        )
    )

    private fun lesPrieres() = Category(
        id = 3,
        title = "Les 5 Prières",
        subtitle = "Guide complet de la Salat",
        iconRes = R.drawable.ic_prayer,
        sections = listOf(
            Section(
                heading = "Les prières et leurs horaires",
                items = listOf(
                    ContentItem(
                        title = "Fajr — L'aube",
                        description = "De l'aube au lever du soleil. 2 rak'ats. C'est la prière la plus méritoire car elle se fait au moment où la plupart des gens dorment."
                    ),
                    ContentItem(
                        title = "Dhouhr — Midi",
                        description = "Après le passage du soleil au zénith jusqu'à l'ombre d'un objet égale à sa hauteur. 4 rak'ats. Prière de midi quand le travail s'arrête pour se tourner vers Allah."
                    ),
                    ContentItem(
                        title = "Asr — Après-midi",
                        description = "Jusqu'au coucher du soleil. 4 rak'ats. Le Prophète ﷺ a dit : « Celui qui prie Asr, sa maison sera en feu (par égard à l'importance de ne pas la manquer). »"
                    ),
                    ContentItem(
                        title = "Maghrib — Coucher du soleil",
                        description = "Au coucher du soleil, avant le crépuscule complet. 3 rak'ats. Prière rapide et courte pour saisir le moment béni."
                    ),
                    ContentItem(
                        title = "Isha — Nuit",
                        description = "Après le crépuscule complet jusqu'au milieu de la nuit. 4 rak'ats. Prière nocturne qui clôt la journée."
                    )
                )
            ),
            Section(
                heading = "Les étapes de la prière",
                items = listOf(
                    ContentItem(
                        title = "1. L'intention (Niyyah)",
                        description = "Avoir l'intention dans le cœur de prier la prière obligatoire en suivant l'exemple du Prophète ﷺ."
                    ),
                    ContentItem(
                        title = "2. Le Takbiratou Ihram",
                        arabic = "ٱللَّٰهُ أَكْبَر",
                        description = "Lever les mains au niveau des épaules en prononçant « Allahou Akbar ». On entre ainsi dans l'état de sacralité de la prière."
                    ),
                    ContentItem(
                        title = "3. La récitation de la Fatiha",
                        arabic = "ٱلْحَمْدُ لِلَّٰهِ رَبِّ ٱلْعَٰلَمِينَ ۝ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ۝ مَٰلِكِ يَوْمِ ٱلدِّينِ",
                        description = "Réciter la Sourate Al-Fatiha, sourate essentielle sans laquelle la prière n'est pas valide. Puis réciter une autre sourate dans les deux premiers rak'ats."
                    ),
                    ContentItem(
                        title = "4. Le Ruku (Inclinaison)",
                        description = "S'incliner le dos droit en disant « Subhana Rabbiyal Adhim » (Gloire à mon Seigneur le Très-Grand) trois fois."
                    ),
                    ContentItem(
                        title = "5. Le Djoujou (Debout)",
                        description = "Se redresser en disant « Sami Allahou liman hamidah », puis « Rabbana wa lakal hamd »."
                    ),
                    ContentItem(
                        title = "6. Le Soudjouj (Prosternation)",
                        arabic = "سُبْحَانَ رَبِّيَ ٱلْأَعْلَىٰ",
                        description = "Se prosterner sur le front, le nez, les paumes, les genoux et les orteils en disant « Subhana Rabbiyal A'la » (Gloire à mon Seigneur le Très-Haut). C'est le moment où le serviteur est le plus proche d'Allah."
                    ),
                    ContentItem(
                        title = "7. Le Tashahoud",
                        arabic = "ٱلتَّحِيَّاتُ لِلَّٰهِ وَٱلصَّلَوَاتُ وَٱلطَّيِّبَات",
                        description = "S'asseoir et témoigner : « Ash-hadou al-la ilaha illa Allah wa ash-hadou anna Muhammadan abduhu wa rasoulouh. »"
                    ),
                    ContentItem(
                        title = "8. Le Salam",
                        description = "Tourner la tête à droite puis à gauche en disant « Assalamou alaykoum wa rahmatullah », achevant ainsi la prière."
                    )
                )
            ),
            Section(
                heading = "La woudou' (Ablutions)",
                items = listOf(
                    ContentItem(
                        title = "Les étapes des ablutions",
                        arabic = "بِسْمِ ٱللَّٰهِ",
                        description = "Dire Bismillah, laver les mains 3 fois, la bouche 3 fois, les narines 3 fois, le visage 3 fois, les avant-bras 3 fois, passer les mains mouillées sur la tête et les oreilles, puis laver les pieds 3 fois. Terminer par le témoignage : Ash-hadou an la ilaha illa Allah wahdahu la sharika lah, wa ash-hadou anna Muhammadan abduhu wa rasoulouh."
                    )
                )
            )
        )
    )

    private fun invocations() = Category(
        id = 4,
        title = "Invocations (Douas)",
        subtitle = "Douas du Coran et de la Sunna",
        iconRes = R.drawable.ic_dua,
        sections = listOf(
            Section(
                heading = "Douas quotidiennes",
                items = listOf(
                    ContentItem(
                        title = "Au lever",
                        arabic = "ٱلْحَمْدُ لِلَّٰهِ ٱلَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ ٱلنُّشُور",
                        description = "« Louange à Allah qui nous a redonnés la vie après nous avoir fait mourir, et c'est vers Lui que sera la résurrection. »"
                    ),
                    ContentItem(
                        title = "Avant de sortir de la maison",
                        arabic = "بِسْمِ ٱللَّٰهِ تَوَكَّلْتُ عَلَى ٱللَّٰهِ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِٱللَّٰه",
                        description = "« Au nom d'Allah, je m'en remets à Allah, et il n'y a de force ni de puissance qu'en Allah. »"
                    ),
                    ContentItem(
                        title = "Avant de manger",
                        arabic = "بِسْمِ ٱللَّٰهِ",
                        description = "Dire « Bismillah » avant de manger. Si on a oublié, dire : « Bismillahi fi awwalihi wa akhirihi. »"
                    ),
                    ContentItem(
                        title = "Après le repas",
                        arabic = "ٱلْحَمْدُ لِلَّٰهِ ٱلَّذِي أَطْعَمَنِي هَٰذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِّنِّي وَلَا قُوَّةٍ",
                        description = "« Louange à Allah qui m'a nourri de ceci et m'a pourvu sans force de ma part ni de puissance. »"
                    ),
                    ContentItem(
                        title = "Avant de dormir",
                        arabic = "بِاسْمِكَ ٱللَّٰهُمَّ أَمُوتُ وَأَحْيَا",
                        description = "Dire « Bismika Allahoumma amoutou wa ayah » (C'est en Ton nom, ô Allah, que je meurs et que je vis), puis essuyer le visage 3 fois."
                    ),
                    ContentItem(
                        title = "Contre le mal du œil",
                        arabic = "أَعُوذُ بِكَلِمَاتِ ٱللَّٰهِ ٱلتَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
                        description = "« Je cherche refuge dans les mots parfaits d'Allah contre le mal de ce qu'Il a créé. » À réciter 3 fois le matin et le soir."
                    ),
                    ContentItem(
                        title = "Doua après la prière",
                        arabic = "ٱللَّٰهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِك",
                        description = "« Ô Allah, aide-moi à Ton souvenir, à Te remercier et à bien T'adorer. »"
                    )
                )
            ),
            Section(
                heading = "Douas du Coran",
                items = listOf(
                    ContentItem(
                        title = "Doua de Nuh (Noé) ﷺ",
                        arabic = "رَبِّ ٱغْفِرْ لِي وَلِوَٰلِدَيَّ وَلِمَن دَخَلَ بَيْتِيَ ٱلْمُؤْمِنِينَ",
                        description = "« Mon Seigneur, pardonne-moi, à mes parents, à celui qui entre dans ma maison en croyant. » (Sourate Nuh, 71:28)"
                    ),
                    ContentItem(
                        title = "Doua de Moussa (Moïse) ﷺ",
                        arabic = "رَبِّ ٱشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي وَٱحْلُلْ عُقْدَةً مِّن لِّسَانِي يَفْقَهُوا قَوْلِي",
                        description = "« Mon Seigneur, élargis ma poitrine, facilite mon affaire et dénoue un nœud sur ma langue pour qu'ils comprennent mes paroles. » (Sourate Ta-Ha, 20:25-28)"
                    ),
                    ContentItem(
                        title = "Doua de Maryam (Marie) ﷺ",
                        arabic = "رَبِّ إِنِّي نَذَرْتُ لَكَ مَا فِي بَطْنِي مُحَرَّرًا فَتَقَبَّلْ مِنِّي",
                        description = "« Mon Seigneur, j'ai voué à Toi ce qui est dans mon ventre, consacré ; accepte donc de moi. » (Sourate Al-Imran, 3:35)"
                    )
                )
            ),
            Section(
                heading = "Sous-titres et rappels",
                items = listOf(
                    ContentItem(
                        title = "Astaghfirullah",
                        arabic = "أَسْتَغْفِرُ ٱللَّٰه",
                        description = "« Je demande le pardon d'Allah. » Le Prophète ﷺ disait cette invocation plus de 70 fois par jour. C'est une source de pardon, de paix et de bénédictions."
                    ),
                    ContentItem(
                        title = "SubhanAllah, Alhamdulillah, Allahou Akbar",
                        arabic = "سُبْحَانَ ٱللَّٰهِ ۝ ٱلْحَمْدُ لِلَّٰهِ ۝ ٱللَّٰهُ أَكْبَر",
                        description = "« Gloire à Allah, louange à Allah, Allah est le plus Grand. » Trois paroles chères au Prophète ﷺ : les 33 répétées après chaque prière effacent les péchés."
                    ),
                    ContentItem(
                        title = "La shahadateyn",
                        arabic = "سُبْحَانَ ٱللَّٰهِ وَبِحَمْدِهِ وَسُبْحَانَ ٱللَّٰهِ ٱلْعَظِيم",
                        description = "« Gloire à Allah et louange à Allah, et gloire à Allah le Très-Grand. » Deux paroles légères sur la langue, lourdes dans la balance, chères au Tout-Miséricordieux."
                    )
                )
            )
        )
    )

    private fun souratesCourtes() = Category(
        id = 5,
        title = "Sourates Courtes",
        subtitle = "Les plus courtes du Saint Coran",
        iconRes = R.drawable.ic_quran,
        sections = listOf(
            Section(
                heading = "Sourate Al-Fatiha (L'Ouverture)",
                items = listOf(
                    ContentItem(
                        title = "Sourate 1 — Al-Fatiha",
                        arabic = "بِسْمِ ٱللَّٰهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ۝ ٱلْحَمْدُ لِلَّٰهِ رَبِّ ٱلْعَٰلَمِينَ ۝ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ۝ مَٰلِكِ يَوْمِ ٱلدِّينِ ۝ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ۝ ٱهْدِنَا ٱلصِّرَٰطَ ٱلْمُسْتَقِيمَ ۝ صِرَٰطَ ٱلَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ ٱلْمَغْضُوبِ عَلَيْهِمْ وَلَا ٱلضَّآلِّين",
                        description = "Au nom d'Allah, le Tout Miséricordieux, le Très Miséricordieux. Louange à Allah, Seigneur de l'univers. Le Tout Miséricordieux, le Très Miséricordieux, Maître du Jour de la rétribution. C'est Toi [Seul] que nous adorons, et c'est Toi [Seul] dont nous implorons secours. Guide-nous dans le droit chemin, le chemin de ceux que Tu as comblés de faveurs, non pas de ceux qui ont encouru Ta colère, ni des égarés."
                    )
                )
            ),
            Section(
                heading = "Sourates du Juz 'Amma",
                items = listOf(
                    ContentItem(
                        title = "Sourate 112 — Al-Ikhlas (Le Monothéisme Pur)",
                        arabic = "قُلْ هُوَ ٱللَّٰهُ أَحَدٌ ۝ ٱللَّٰهُ ٱلصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُۥ كُفُوًا أَحَدٌ",
                        description = "Dis : « Il est Allah, Unique. Allah, Le Seul à être imploré pour ce que nous désirons. Il n'a jamais engendré, n'a pas été engendré non plus. Et nul n'est égal à Lui. »"
                    ),
                    ContentItem(
                        title = "Sourate 113 — Al-Falaq (L'Aube Naissante)",
                        arabic = "قُلْ أَعُوذُ بِرَبِّ ٱلْفَلَقِ ۝ مِن شَرِّ مَا خَلَقَ ۝ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ۝ وَمِن شَرِّ ٱلنَّفَّٰثَٰتِ فِى ٱلْعُقَدِ ۝ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ",
                        description = "Dis : « Je cherche protection auprès du Seigneur de l'aube naissante, contre le mal des êtres qu'Il a créés, contre le mal de l'obscurité quand elle s'approfondit, contre le mal de celles qui soufflent sur les nœuds, et contre le mal de l'envieux quand il envie. »"
                    ),
                    ContentItem(
                        title = "Sourate 114 — An-Nas (Les Hommes)",
                        arabic = "قُلْ أَعُوذُ بِرَبِّ ٱلنَّاسِ ۝ مَلِكِ ٱلنَّاسِ ۝ إِلَٰهِ ٱلنَّاسِ ۝ مِن شَرِّ ٱلْوَسْوَاسِ ٱلْخَنَّاسِ ۝ ٱلَّذِي يُوَسْوِسُ فِي صُدُورِ ٱلنَّاسِ ۝ مِنَ ٱلْجِنَّةِ وَٱلنَّاس",
                        description = "Dis : « Je cherche protection auprès du Seigneur des hommes, le Souverain des hommes, le Dieu des hommes, contre le mal des mauvais conseillers, furtifs, qui soufflent le mal dans les poitrines des hommes, qu'ils soient des djinns ou des hommes. »"
                    ),
                    ContentItem(
                        title = "Sourate 110 — An-Nasr (Le Secours)",
                        arabic = "إِذَا جَاءَ نَصْرُ ٱللَّٰهِ وَٱلْفَتْحُ ۝ وَرَأَيْتَ ٱلنَّاسَ يَدْخُلُونَ فِى دِينِ ٱللَّٰهِ أَفْوَاجًا ۝ فَسَبِّحْ بِحَمْدِ رَبِّكَ وَٱسْتَغْفِرْهُ ی إِنَّهُۥ كَانَ تَوَّابًۢا",
                        description = "Quand vient le secours d'Allah ainsi que la victoire, et que tu vois les gens entrer en masse dans la religion d'Allah, alors célèbre la louange de ton Seigneur et implore Son pardon. Car c'est Lui le grand Accueillant au repentir."
                    ),
                    ContentItem(
                        title = "Sourate 108 — Al-Kawthar (L'Abondance)",
                        arabic = "إِنَّآ أَعْطَيْنَٰكَ ٱلْكَوْثَرَ ۝ فَصَلِّ لِرَبِّكَ وَٱنْحَرْ ۝ إِنَّ شَانِئَكَ هُوَ ٱلْأَبْتَرُ",
                        description = "Nous t'avons certes accordé Al-Kawthar. Accomplis donc la prière pour ton Seigneur et sacrifie. Celui qui te hait sera certes sans postérité."
                    ),
                    ContentItem(
                        title = "Sourate 103 — Al-Asr (Le Temps)",
                        arabic = "وَٱلْعَصْرِ ۝ إِنَّ ٱلْإِنسَٰنَ لَفِى خُسْرٍ ۝ إِلَّا ٱلَّذِينَ ءَامَنُوا۟ وَعَمِلُوا۟ ٱلصَّٰلِحَٰتِ وَتَوَاصَوْا۟ بِٱلْحَقِّ وَتَوَاصَوْا۟ بِٱلصَّبْر",
                        description = "Par le Temps ! L'homme est certes en perdition, sauf ceux qui croient et accomplissent les bonnes œuvres, s'enjoignent mutuellement la vérité et s'enjoignent mutuellement l'endurance."
                    ),
                    ContentItem(
                        title = "Sourate 105 — Al-Fil (L'Éléphant)",
                        description = "Récit du peuple des Éléphants protégé par les oiseaux Ababil qui protégèrent la Kaaba de l'attaque d'Abraha. Sourate de la protection divine."
                    ),
                    ContentItem(
                        title = "Sourate 111 — Al-Masadd (Les Fibres)",
                        description = "Sourate contre Abu Lahab et sa femme, décrivant leur punition dans le Feu. Preuve de la véracité du Prophète ﷺ."
                    ),
                    ContentItem(
                        title = "Sourate 109 — Al-Kafirun (Les Infidèles)",
                        description = "« À vous votre religion, et à moi ma religion. » Sourate affirmant la pureté de l'Islam face à tout polythéisme."
                    ),
                    ContentItem(
                        title = "Sourate 97 — Al-Qadr (La Destinée)",
                        description = "Décrivant la Nuit du Destin (Laylatou Al-Qadr), meilleure que mille mois, où la révélation du Coran commença. Les anges et l'Esprit y descendent par permission de leur Seigneur."
                    ),
                    ContentItem(
                        title = "Sourate 94 — Ash-Sharh (L'Ouverture de la Poitrine)",
                        description = "Allah raconte comment Il a ouvert la poitrine du Prophète ﷺ, a allégé son fardeau et a élevé son renom. Après la difficulté vient la facilité."
                    )
                )
            )
        )
    )

    private fun rappelsEtBienfaits() = Category(
        id = 6,
        title = "Rappels & Bienfaits",
        subtitle = "Sounna et enseignements quotidiens",
        iconRes = R.drawable.ic_reminder,
        sections = listOf(
            Section(
                heading = "Bienfaits de l'Islam",
                items = listOf(
                    ContentItem(
                        title = "La paix intérieure",
                        description = "Le rappel d'Allah (dhikr) apaise le cœur. Allah dit : « N'est-ce pas dans le rappel d'Allah que les cœurs trouvent la quiétude ? » (13:28). La prière quotidienne ancre le croyant dans la sérénité."
                    ),
                    ContentItem(
                        title = "La communauté (Oumma)",
                        description = "Le vendredi, les prières en congregation, le Hajj et l'Zakat créent un lien fort entre les musulmans. Le Prophète ﷺ a dit : « Les croyants dans leur amour mutuel, leur miséricorde et leur compassion sont comme un seul corps. »"
                    ),
                    ContentItem(
                        title = "L'équilibre dans la vie",
                        description = "L'Islam équilibre l'adoration, la famille, le travail et la société. Il encourage le savoir, la justice, la protection des faibles et le respect de la création."
                    )
                )
            ),
            Section(
                heading = "Sounna du Prophète ﷺ",
                items = listOf(
                    ContentItem(
                        title = "Le sourire",
                        description = "« Le sourire de ton visage envers ton frère est une aumône. » (Tirmidhi). Un acte simple mais transformationnel."
                    ),
                    ContentItem(
                        title = "Dire Bismillah",
                        description = "Commencer toute action par le nom d'Allah : manger, sortir, écrire, etc. C'est une bénédiction (baraka) dans chaque geste."
                    ),
                    ContentItem(
                        title = "Honorer ses parents",
                        description = "« Le Seigneur a interdit toute désobéissance aux parents. » Le Prophète ﷺ a placé l'obéissance aux parents juste après l'adoration d'Allah."
                    ),
                    ContentItem(
                        title = "Être bon envers les voisins",
                        description = "« Jibril n'a cessé de me recommander le voisin jusqu'à ce que j'ai cru qu'il allait en faire un héritier. » (Boukhari)"
                    ),
                    ContentItem(
                        title = "La pureté (Tahara)",
                        description = "« La pureté est la moitié de la foi. » Garder la propreté du corps, des vêtements et de l'environnement est un acte d'adoration."
                    ),
                    ContentItem(
                        title = "Dire Alhamdulillah",
                        description = "Remercier Allah dans toutes les situations. Le Prophète ﷺ a dit : « Étonnant est le cas du croyant : tout ce qui lui arrive est un bien, et cela n'arrive à personne d'autre. »"
                    )
                )
            ),
            Section(
                heading = "Les dix commandements du Prophète ﷺ",
                items = listOf(
                    ContentItem(
                        title = "Hadith de Ibni Mas'oud",
                        description = "1. Adorer Allah sans rien Lui associer. 2. Accomplir la prière. 3. Acquitter la Zakat. 4. Jeûner Ramadan. 5. Faire le Hajj si on en a les moyens. 6. Être bon envers ses parents. 7. Agir en bien. 8. Abandonner l'adultère. 9. Ne pas tuer. 10. Ne pas voler."
                    )
                )
            ),
            Section(
                heading = "Les 99 Noms d'Allah",
                items = listOf(
                    ContentItem(
                        title = "Les Noms les plus connus",
                        description = "Ar-Rahman (Le Tout Miséricordieux), Ar-Rahim (Le Très Miséricordieux), Al-Malik (Le Souverain), Al-Quddous (Le Très Pur), As-Salam (Le Source de Paix), Al-Moumin (Le Garant de Sécurité), Al-Aziz (Le Tout Puissant), Al-Khaliq (Le Créateur), Ar-Razzaq (Le Pourvoyeur), Al-Hakim (Le Sage), Al-Wadoud (Le Très Aimant), Al-Ghafour (Le Très Pardonneur)."
                    ),
                    ContentItem(
                        title = "Invocation des Noms",
                        description = "Allah dit : « Celui qui M'invoque par Mes noms Je lui réponds. » Apprendre et invoquer les beaux noms d'Allah est une source de réponse à nos prières."
                    )
                )
            )
        )
    )
    fun getCategoryById(id: Int): Category? = getCategories().find { it.id == id }
}

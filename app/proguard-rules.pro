# Préserver les modèles pour Gson (les noms de champs correspondent au JSON)
-keep class com.sourate.app.Surah { *; }
-keep class com.sourate.app.Ayah { *; }
-keep class com.sourate.app.XassidaMeta { *; }
-keep class com.sourate.app.XassidaVerse { *; }
-keep class com.sourate.app.XassidaDetail { *; }
-keep class com.sourate.app.XassidaRepository$ListWrapper { *; }

# Règles Gson génériques
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

-dontwarn com.google.gson.**

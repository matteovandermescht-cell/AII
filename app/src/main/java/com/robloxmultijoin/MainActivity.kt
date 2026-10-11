package com.robloxmultijoin

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("multi_join", MODE_PRIVATE) }
    private lateinit var status: TextView
    private lateinit var linkInput: EditText
    private val accountInputs = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(15, 17, 24)
        window.navigationBarColor = Color.rgb(15, 17, 24)
        buildUi()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            setBackgroundColor(Color.rgb(15, 17, 24))
        }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(content)

        content.addView(label("Roblox Multi-Join", 26, Color.WHITE))
        content.addView(label("Bis zu 4 Account-Profile · keine Passwörter gespeichert", 14, Color.LTGRAY))
        content.addView(space(18))
        content.addView(label("Account-Namen (optional)", 17, Color.WHITE))

        for (i in 1..4) {
            val input = EditText(this).apply {
                hint = "Account $i – Benutzername"
                setSingleLine(true)
                setTextColor(Color.WHITE)
                setHintTextColor(Color.GRAY)
                setPadding(dp(12), dp(8), dp(12), dp(8))
                setBackgroundColor(Color.rgb(31, 35, 46))
                setText(prefs.getString("account_$i", ""))
            }
            accountInputs.add(input)
            content.addView(input, matchWrap())
            content.addView(space(8))
        }

        content.addView(Button(this).apply {
            text = "Account-Namen lokal speichern"
            setOnClickListener {
                val editor = prefs.edit()
                accountInputs.forEachIndexed { index, edit ->
                    editor.putString("account_${index + 1}", edit.text.toString().trim())
                }
                editor.apply()
                status.text = "Gespeichert: nur Account-Namen, keine Passwörter."
            }
        })
        content.addView(space(18))
        content.addView(label("Roblox-Spiel-Link", 17, Color.WHITE))

        linkInput = EditText(this).apply {
            hint = "https://www.roblox.com/games/..."
            setSingleLine(true)
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            setPadding(dp(12), dp(8), dp(12), dp(8))
            setBackgroundColor(Color.rgb(31, 35, 46))
            setText(prefs.getString("game_link", ""))
        }
        content.addView(linkInput, matchWrap())
        content.addView(space(8))

        content.addView(Button(this).apply {
            text = "Spiel direkt in Roblox öffnen"
            setOnClickListener { openExperience() }
        })
        content.addView(space(12))

        status = label("Status: Noch kein Spiel-Link geöffnet. Diese App speichert keine Roblox-Passwörter.", 14, Color.rgb(190, 200, 215))
        content.addView(status)
        content.addView(space(16))
        content.addView(label("Sicherheit: Gib dein Roblox-Passwort nur in der offiziellen Roblox-App oder auf roblox.com ein – niemals in dieser App. Mehrere getrennte Roblox-Sitzungen auf einem Handy werden nicht garantiert.", 13, Color.LTGRAY))
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun openExperience() {
        val raw = linkInput.text.toString().trim()
        if (raw.isBlank()) {
            status.text = "Bitte zuerst einen Roblox-Spiel-Link eingeben."
            return
        }
        val uri = runCatching { Uri.parse(raw) }.getOrNull()
        val valid = uri != null &&
            uri.scheme == "https" &&
            (uri.host == "www.roblox.com" || uri.host == "roblox.com" || uri.host == "m.roblox.com") &&
            uri.path?.startsWith("/games/") == true
        if (!valid) {
            status.text = "Ungültiger Link. Nutze einen HTTPS-Link zu roblox.com/games/..."
            return
        }

        val placeId = Regex("/games/(\\d+)").find(uri.path.orEmpty())?.groupValues?.getOrNull(1)
        prefs.edit().putString("game_link", raw).apply()

        try {
            if (placeId != null) {
                val appUri = Uri.parse("roblox://experiences/start?placeId=$placeId")
                startActivity(Intent(Intent.ACTION_VIEW, appUri))
                status.text = "Roblox-App wurde angefordert. Bitte prüfe, ob das Spiel geöffnet wird. Kein Passwort wird gespeichert."
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, uri))
                status.text = "Link geöffnet. Falls der Browser erscheint, tippe dort auf Spielen. Kein Passwort wird gespeichert."
            }
        } catch (_: Exception) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, uri))
                status.text = "Roblox-App konnte nicht direkt geöffnet werden; Link im Browser geöffnet. Kein Passwort wird gespeichert."
            } catch (_: Exception) {
                status.text = "Roblox konnte nicht geöffnet werden. Prüfe, ob Roblox installiert ist."
            }
        }
    }

    private fun label(text: String, size: Int, color: Int) = TextView(this).apply {
        this.text = text
        textSize = size.toFloat()
        setTextColor(color)
        if (size >= 20) setTypeface(null, android.graphics.Typeface.BOLD)
        setPadding(0, dp(4), 0, dp(4))
    }

    private fun space(height: Int) = TextView(this).apply { this.height = dp(height) }
    private fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}

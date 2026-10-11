package com.robloxmultijoin

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.view.Gravity
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
        content.addView(label("Bis zu 4 Account-Profile · ohne Passwörter", 14, Color.LTGRAY))
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

        val save = Button(this).apply {
            text = "Account-Namen lokal speichern"
            setOnClickListener {
                val editor = prefs.edit()
                accountInputs.forEachIndexed { index, edit ->
                    editor.putString("account_${index + 1}", edit.text.toString().trim())
                }
                editor.apply()
                status.text = "Gespeichert. Es wurden keine Passwörter gespeichert."
            }
        }
        content.addView(save)
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
            text = "Spiel in Roblox öffnen"
            setOnClickListener { openExperience() }
        })
        content.addView(space(12))

        status = label("Status: Noch kein Spiel-Link geöffnet. Live-Spielstatus ist nicht automatisch verfügbar.", 14, Color.rgb(190, 200, 215))
        content.addView(status)
        content.addView(space(16))
        content.addView(label("Hinweis: Ein Handy kann nicht garantiert zwei getrennte Roblox-Sitzungen gleichzeitig ausführen. Dieser Prototyp öffnet den Spiel-Link und meldet keinen ungeprüften Live-Status.", 13, Color.LTGRAY))
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
        prefs.edit().putString("game_link", raw).apply()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(intent)
            status.text = "Spiel-Link an Android übergeben. Ob der Account im Spiel ist, kann diese App nicht sicher bestätigen."
        } catch (_: Exception) {
            status.text = "Roblox konnte nicht geöffnet werden. Prüfe, ob Roblox installiert ist."
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

---
marp: true
theme: default
paginate: true
backgroundColor: #fff
style: |
  section { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
  h1 { color: #1d72b8; }
  h2 { color: #333; }
  code { background: #f4f4f4; padding: 2px 6px; border-radius: 4px; }
---

# Autenticazione JWT & Sicurezza Stateless
Implementazione in Spring Boot 3 + React (Vite)  
👤 Relatore: Giovanni Contestabile | 📅 11/05/2026

---

# Agenda
- Cos'è JWT e perché lo usiamo
- Architettura Client/Server
- Flusso di Login & Validazione
- Access + Refresh Token
- Componenti Backend & Frontend
- Sicurezza: XSS, CSRF, Autorizzazione
- Best Practice & Produzione

---

# Cos'è JWT?
- Standard RFC 7519 per scambio sicuro di claim
- Struttura: `Header.Payload.Signature`
- ✅ Integrità garantita dalla firma crittografica
- ✅ Stateless: nessuna sessione memorizzata lato server
- ⚠️ Payload è Base64, **NON cifrato** → mai dati sensibili

---

# Architettura del Sistema
[React Frontend] ←→ [Spring Boot API]
       ↑                      ↑
  Cookie HttpOnly        JWT Filter
  (access & refresh)   (valida firma)
  - Comunicazione via **Cookie `HttpOnly`** (NO `localStorage`)
- Backend **Stateless** → scalabilità orizzontale
- Single Source of Truth: endpoint `/auth/me`

---

# Flusso di Autenticazione
1. `POST /api/auth/login` → credenziali validate
2. Backend genera Access (15m) + Refresh (7g) → `Set-Cookie`
3. Frontend chiama `/auth/me` → aggiorna `AuthContext`
4. Ogni richiesta API include cookie (`withCredentials: true`)
5. `JwtAuthFilter` verifica firma → popola `SecurityContext`

---

# Backend: Spring Security & JWT
- `SecurityConfig`: Stateless, CORS stretto, CSRF disabilitato (sicuro per API)
- `JwtAuthFilter`: Estrae token da cookie, valida, imposta autenticazione
- `@PreAuthorize("hasRole('ADMIN')")`: autorizzazione **lato server**
- 🔑 Regola d'oro: Ruoli frontend = solo UX. Mai logica di sicurezza lato client.

---

# Frontend: Gestione Stato & Routing
- `axios.js`: `withCredentials: true`, interceptor risposte
- `AuthContext`: carica utente da `/auth/me`, gestisce login/logout
- `ProtectedRoute`: wrapper React Router, verifica stato/ruoli
- ✅ Decoding JWT lato client **EVITATO** → più sicuro e affidabile

---

# Access + Refresh Token: Perché 2?
| Token | Durata | Scopo | Rischio |
|-------|--------|-------|---------|
| Access | Breve (15m) | Autenticare API | Basso |
| Refresh | Lunga (7g) | Rinnovare access | Medio (revocabile) |
- Vantaggio: UX fluida + finestra di esposizione ridotta

---

# Refresh Automatico (Interceptor)
- 401 rilevato → Interceptor blocca richiesta
- Chiama `/auth/refresh` (cookie refresh inviato)
- `failedQueue`: accoda richieste parallele, evita race condition
- Al successo: nuovo access cookie → ripete richiesta originale
- Fallimento → redirect `/login`

---

# Sicurezza Applicata (Mitigazioni)
- 🛡️ **XSS**: Cookie `HttpOnly` → JS non legge token
- 🛡️ **CSRF**: `SameSite=Lax` + API stateless
- 🛡️ **Replay Attack**: `exp` breve + validazione firma
- 🛡️ **Privilege Escalation**: `@PreAuthorize` lato server
- 🛡️ **CORS**: Origini esplicite, mai `*`, `allowCredentials: true`

---

# Principi Architetturali Chiave
- ✅ **Stateless**: Scala orizzontalmente, nessun sticky session
- ✅ **Defense-in-Depth**: Transport → Storage → Validazione → Autorizzazione
- ✅ **Server-Side Truth**: Frontend = UX, Backend = Sicurezza
- ✅ **Automatic UX**: Refresh trasparente, zero logout indesiderati

---

# Prossimi Passi (Produzione)
- 🔁 Refresh Token Rotation + Invalidazione DB
- 📊 Rate Limiting (`/login`, `/refresh`) → Bucket4j/Redis
- 🔒 HTTPS obbligatorio → `cookie.secure=true`
- 📝 Audit Log & Monitoring tentativi falliti
- 🧪 Test di sicurezza (OWASP ZAP, Penetration Test)

---

# Q&A
- **Domande?**  
- 📧 Contatti: gio.contestabile@gmail.com 
- 📚 Risorse: Spring Security Docs, RFC 7519, OWASP JWT Cheat Sheet
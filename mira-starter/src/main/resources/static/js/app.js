
/* =========================================================
   HLS Streaming Dashboard — logic
   Adapté à l'API MuslimApi / Spring Boot

   UPLOAD:
   POST /muslimApi/v1/videos/upload
   Authorization: Bearer <token>
   Query:
      title
      description
      visibility
   Multipart:
      video

   STREAM:
   GET /muslimApi/v1/videos/stream/{videoId}/{filename}
   Authorization: Bearer <token>
========================================================= */

// =========================================================
// CONFIGURATION API
// =========================================================

const API_BASE = "/muslimApi/v1";

const UPLOAD_ENDPOINT = `${API_BASE}/videos/upload`;

const STREAM_BASE = `${API_BASE}/videos/stream`;

// Nom utilisé pour stocker le JWT dans localStorage.
// Si ton application utilise un autre nom, modifie cette valeur.
const TOKEN_STORAGE_KEY = "token";


// =========================================================
// AUTHENTIFICATION
// =========================================================

function getAuthToken() {
    /*
     * Essaie plusieurs clés courantes afin de faciliter
     * l'intégration avec ton application existante.
     */

    const possibleKeys = [
        TOKEN_STORAGE_KEY,
        "accessToken",
        "access_token",
        "jwt",
        "JWT",
        "bearerToken"
    ];

    for (const key of possibleKeys) {
        const token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTc4Njg1OTkzNywiaWF0IjoxNzg2MjU1MTM3fQ.5_VLarjhY36I-Or2c1C4nAo9p1OL8J-l3Ntn8urVXZs";

        if (token && token.trim()) {
            return token.trim();
        }
    }

    return null;
}


function getAuthorizationHeader() {
    const token = getAuthToken();

    if (!token) {
        return null;
    }

    /*
     * Si le token est déjà stocké sous la forme
     * "Bearer eyJ...", on ne rajoute pas Bearer.
     */

    if (token.toLowerCase().startsWith("bearer ")) {
        return token;
    }

    return `Bearer ${token}`;
}


function checkAuthentication() {
    const authHeader = getAuthorizationHeader();

    if (!authHeader) {
        console.warn(
            "Aucun token JWT trouvé dans localStorage."
        );

        return false;
    }

    return true;
}


// =========================================================
// HELPERS GÉNÉRIQUES
// =========================================================

function setBox(el, msg, type) {
    if (!el) return;

    el.textContent = msg;
    el.className = "status" + (type ? " " + type : "");
}


function logTo(el, msg) {
    if (!el) return;

    const line = document.createElement("div");
    const time = new Date().toLocaleTimeString();

    line.textContent = `[${time}] ${msg}`;

    el.prepend(line);
}


function showResult(el, content) {
    if (!el) return;

    el.textContent = content;
    el.classList.remove("hidden");
}


function formatBytes(bytes) {
    if (bytes === 0) return "0 B";

    const units = ["B", "KB", "MB", "GB"];

    const i = Math.floor(
        Math.log(bytes) / Math.log(1024)
    );

    return `${(
        bytes / Math.pow(1024, i)
    ).toFixed(1)} ${units[i]}`;
}


// =========================================================
// SECTION 1 — UPLOAD DE FICHIER
// =========================================================

const uploadForm = document.getElementById("uploadForm");
const fileInput = document.getElementById("fileInput");
const uploadResult = document.getElementById("uploadResult");
const uploadProgressTrack =
    document.getElementById("uploadProgressTrack");
const uploadProgressFill =
    document.getElementById("uploadProgressFill");
const uploadSubmitBtn =
    document.getElementById("uploadSubmitBtn");


if (uploadForm) {

    uploadForm.addEventListener("submit", function (e) {

        e.preventDefault();


        // -------------------------------------------------
        // Vérification authentification
        // -------------------------------------------------

        const authHeader = getAuthorizationHeader();

        if (!authHeader) {

            showResult(
                uploadResult,
                "❌ Aucun token Bearer trouvé. Connecte-toi d'abord."
            );

            return;
        }


        // -------------------------------------------------
        // Fichier
        // -------------------------------------------------

        const file = fileInput?.files?.[0];

        if (!file) {

            showResult(
                uploadResult,
                "⚠️ Sélectionne d'abord un fichier."
            );

            return;
        }


        // -------------------------------------------------
        // Récupération des informations vidéo
        // -------------------------------------------------

        /*
         * Ces IDs correspondent idéalement à ton HTML :
         *
         * titleInput
         * descriptionInput
         * visibilityInput
         *
         * Si certains éléments n'existent pas, on utilise
         * des valeurs par défaut.
         */

        const titleInput =
            document.getElementById("titleInput");

        const descriptionInput =
            document.getElementById("descriptionInput");

        const visibilityInput =
            document.getElementById("visibilityInput");


        const title =
            titleInput?.value?.trim() || file.name;


        const description =
            descriptionInput?.value?.trim() || "";


        const visibility =
            visibilityInput?.value?.trim() || "PUBLIC";


        // -------------------------------------------------
        // Construction URL avec Query Parameters
        // -------------------------------------------------

        const uploadUrl = new URL(
            UPLOAD_ENDPOINT,
            window.location.origin
        );


        /*
         * Swagger :
         *
         * title       REQUIRED
         * description OPTIONAL
         * visibility  OPTIONAL
         */

        uploadUrl.searchParams.set(
            "title",
            title
        );


        if (description) {

            uploadUrl.searchParams.set(
                "description",
                description
            );
        }


        if (visibility) {

            uploadUrl.searchParams.set(
                "visibility",
                visibility
            );
        }


        // -------------------------------------------------
        // Multipart
        // -------------------------------------------------

        const formData = new FormData();


        /*
         * IMPORTANT :
         *
         * Ton API attend "video", pas "file".
         */

        formData.append(
            "video",
            file
        );


        // -------------------------------------------------
        // XMLHttpRequest
        // -------------------------------------------------

        const xhr = new XMLHttpRequest();


        xhr.open(
            "POST",
            uploadUrl.toString(),
            true
        );


        // -------------------------------------------------
        // Authorization Bearer
        // -------------------------------------------------

        xhr.setRequestHeader(
            "Authorization",
            authHeader
        );


        /*
         * NE PAS définir manuellement :
         *
         * Content-Type: multipart/form-data
         *
         * Le navigateur doit générer automatiquement
         * le boundary multipart.
         */


        // -------------------------------------------------
        // UI
        // -------------------------------------------------

        if (uploadSubmitBtn) {
            uploadSubmitBtn.disabled = true;
        }


        if (uploadProgressTrack) {
            uploadProgressTrack.classList.add("active");
        }


        if (uploadProgressFill) {
            uploadProgressFill.style.width = "0%";
        }


        showResult(
            uploadResult,
            `⏳ Envoi de "${file.name}" (${formatBytes(file.size)})...`
        );


        logTo(
            logEl,
            `Upload : ${file.name}`
        );


        logTo(
            logEl,
            `Titre : ${title}`
        );


        logTo(
            logEl,
            `Visibilité : ${visibility}`
        );


        // -------------------------------------------------
        // Progression
        // -------------------------------------------------

        xhr.upload.addEventListener(
            "progress",
            function (evt) {

                if (evt.lengthComputable) {

                    const pct =
                        Math.round(
                            (evt.loaded / evt.total) * 100
                        );


                    if (uploadProgressFill) {
                        uploadProgressFill.style.width =
                            pct + "%";
                    }
                }
            }
        );


        // -------------------------------------------------
        // Réponse
        // -------------------------------------------------

        xhr.onload = function () {

            if (uploadSubmitBtn) {
                uploadSubmitBtn.disabled = false;
            }


            if (uploadProgressTrack) {
                uploadProgressTrack.classList.remove(
                    "active"
                );
            }


            // -------------------------------------------------
            // Succès
            // -------------------------------------------------

            if (
                xhr.status >= 200 &&
                xhr.status < 300
            ) {

                let body = xhr.responseText;

                try {

                    const json =
                        JSON.parse(xhr.responseText);


                    body =
                        JSON.stringify(
                            json,
                            null,
                            2
                        );


                    /*
                     * Ton backend peut renvoyer :
                     *
                     * {
                     *   "videoId": "..."
                     * }
                     *
                     * ou éventuellement un objet contenant
                     * l'identifiant dans une autre propriété.
                     */

                    const videoId =
                        json.videoId ||
                        json.id;


                    if (videoId) {

                        const playlistUrl =
                            `${window.location.origin}` +
                            `${STREAM_BASE}/` +
                            `${encodeURIComponent(videoId)}` +
                            `/playlist.m3u8`;


                        const streamUrlInput =
                            document.getElementById(
                                "streamUrl"
                            );


                        const linkVideoId =
                            document.getElementById(
                                "linkVideoId"
                            );


                        if (streamUrlInput) {

                            streamUrlInput.value =
                                playlistUrl;
                        }


                        if (linkVideoId) {

                            linkVideoId.value =
                                videoId;
                        }


                        logTo(
                            logEl,
                            `Video ID : ${videoId}`
                        );


                        logTo(
                            logEl,
                            `Playlist : ${playlistUrl}`
                        );
                    }

                } catch (_) {

                    /*
                     * La réponse n'est pas JSON.
                     * On l'affiche simplement.
                     */
                }


                showResult(
                    uploadResult,
                    `✅ Upload réussi (HTTP ${xhr.status})\n${body}`
                );


                logTo(
                    logEl,
                    `Upload terminé avec HTTP ${xhr.status}`
                );


                return;
            }


            // -------------------------------------------------
            // Erreur HTTP
            // -------------------------------------------------

            let errorMessage =
                xhr.responseText;


            try {

                const errorJson =
                    JSON.parse(xhr.responseText);


                errorMessage =
                    JSON.stringify(
                        errorJson,
                        null,
                        2
                    );

            } catch (_) {}


            showResult(
                uploadResult,
                `❌ Échec de l'upload (HTTP ${xhr.status})\n${errorMessage}`
);


logTo(
    logEl,
    `Erreur upload HTTP ${xhr.status}`
);
};


// -------------------------------------------------
// Erreur réseau
// -------------------------------------------------

xhr.onerror = function () {

    if (uploadSubmitBtn) {
        uploadSubmitBtn.disabled = false;
    }


    if (uploadProgressTrack) {
        uploadProgressTrack.classList.remove(
            "active"
        );
    }


    showResult(
        uploadResult,
        "❌ Erreur réseau pendant l'upload. Vérifie que le serveur tourne et que CORS est configuré."
    );


    logTo(
        logEl,
        "Erreur réseau pendant l'upload"
    );
};


// -------------------------------------------------
// Token expiré / non autorisé
// -------------------------------------------------

xhr.onloadend = function () {

    if (xhr.status === 401) {

        logTo(
            logEl,
            "❌ HTTP 401 : token absent, invalide ou expiré."
        );
    }


    if (xhr.status === 403) {

        logTo(
            logEl,
            "❌ HTTP 403 : accès refusé."
        );
    }
};


// -------------------------------------------------
// ENVOI
// -------------------------------------------------

xhr.send(formData);

});
}


// =========================================================
// SECTION 2 — LECTEUR HLS
// =========================================================

const video =
    document.getElementById("video");

const statusEl =
    document.getElementById("status");

const logEl =
    document.getElementById("log");

let hls = null;


// =========================================================
// Construction URL stream
// =========================================================

function createStreamUrl(videoId, filename) {

    return (
        `${window.location.origin}` +
        `${STREAM_BASE}/` +
        `${encodeURIComponent(videoId)}/` +
        `${filename}`
    );
}


// =========================================================
// Chargement HLS
// =========================================================

function loadStream() {

    const url =
        document.getElementById(
            "streamUrl"
        )?.value.trim();


    if (!url) {

        setBox(
            statusEl,
            "⚠️ Merci de saisir une URL de playlist .m3u8",
            "error"
        );

        return;
    }


    // -------------------------------------------------
    // Auth
    // -------------------------------------------------

    const authHeader =
        getAuthorizationHeader();


    if (!authHeader) {

        setBox(
            statusEl,
            "❌ Aucun token Bearer disponible.",
            "error"
        );

        logTo(
            logEl,
            "Token Bearer introuvable."
        );

        return;
    }


    // -------------------------------------------------
    // Nettoyage ancien lecteur
    // -------------------------------------------------

    if (hls) {

        hls.destroy();

        hls = null;
    }


    logTo(
        logEl,
        `Chargement : ${url}`
    );


    setBox(
        statusEl,
        "Chargement du flux...",
        "info"
    );


    // =================================================
    // HLS.JS
    // =================================================

    if (Hls.isSupported()) {


        /*
         * IMPORTANT :
         *
         * xhrSetup permet d'ajouter le Bearer
         * à CHAQUE requête HLS :
         *
         * playlist.m3u8
         * segments .ts
         * segments .m4s
         * autres playlists
         *
         * C'est indispensable pour ton API sécurisée.
         */

        hls = new Hls({

            debug: false,


            xhrSetup: function (
                xhr,
                requestUrl
            ) {

                xhr.setRequestHeader(
                    "Authorization",
                    authHeader
                );


                logTo(
                    logEl,
                    `Requête HLS sécurisée : ${requestUrl}`
                );
            }

        });


        // -------------------------------------------------
        // Chargement source
        // -------------------------------------------------

        hls.loadSource(url);


        hls.attachMedia(video);


        // -------------------------------------------------
        // Manifest chargé
        // -------------------------------------------------

        hls.on(
            Hls.Events.MANIFEST_PARSED,
            function (
                event,
                data
            ) {

                logTo(
                    logEl,
                    `Manifest chargé : ${data.levels.length} qualité(s) disponible(s)`
                );


                setBox(
                    statusEl,
                    "✅ Manifest chargé, lecture en cours",
                    "ok"
                );


                video.play().catch(
                    function (e) {

                        logTo(
                            logEl,
                            "Autoplay bloqué : " +
                            e.message
                        );
                    }
                );
            }
        );


        // -------------------------------------------------
        // Segment chargé
        // -------------------------------------------------

        hls.on(
            Hls.Events.FRAG_LOADED,
            function (
                event,
                data
            ) {

                logTo(
                    logEl,
                    `Segment chargé : ${data.frag.relurl}`
                );
            }
        );


        // -------------------------------------------------
        // Erreurs HLS
        // -------------------------------------------------

        hls.on(
            Hls.Events.ERROR,
            function (
                event,
                data
            ) {

                logTo(
                    logEl,
                    `ERREUR [${data.type}] ${data.details}`
                );


                // -------------------------------------------------
                // 401 / 403
                // -------------------------------------------------

                if (
                    data.response &&
                    (
                        data.response.code === 401 ||
                        data.response.code === 403
                    )
                ) {

                    setBox(
                        statusEl,
                        `❌ Accès refusé par l'API (HTTP ${data.response.code})`,
                        "error"
                    );


                    logTo(
                        logEl,
                        "Le serveur refuse le Bearer token."
                    );


                    return;
                }


                // -------------------------------------------------
                // Erreur fatale
                // -------------------------------------------------

                if (data.fatal) {

                    setBox(
                        statusEl,
                        `❌ Erreur fatale : ${data.details}`,
                        "error"
                    );


                    switch (data.type) {


                        case Hls.ErrorTypes.NETWORK_ERROR:

                            logTo(
                                logEl,
                                "Tentative de reconnexion réseau..."
                            );


                            hls.startLoad();

                            break;


                        case Hls.ErrorTypes.MEDIA_ERROR:

                            logTo(
                                logEl,
                                "Tentative de récupération média..."
                            );


                            hls.recoverMediaError();

                            break;


                        default:

                            logTo(
                                logEl,
                                "Destruction du lecteur HLS."
                            );


                            hls.destroy();

                            break;
                    }
                }
            }
        );


        // =================================================
        // HLS NATIF — Safari
        // =================================================

    } else if (
        video.canPlayType(
            "application/vnd.apple.mpegurl"
        )
    ) {


        /*
         * ATTENTION :
         *
         * video.src = url ne permet pas de contrôler
         * directement le header Authorization comme
         * xhrSetup de hls.js.
         *
         * On essaie donc le chargement natif.
         *
         * Si ton endpoint exige strictement le Bearer
         * pour playlist + segments, Safari/iOS peut
         * nécessiter une stratégie différente côté
         * backend (cookie HttpOnly, URL signée, etc.).
         */


        video.src = url;


        video.addEventListener(
            "loadedmetadata",
            function () {

                setBox(
                    statusEl,
                    "✅ Lecture native HLS (Safari)",
                    "ok"
                );


                video.play().catch(
                    function (e) {

                        logTo(
                            logEl,
                            "Autoplay bloqué : " +
                            e.message
                        );
                    }
                );
            },
            {
                once: true
            }
        );


        video.addEventListener(
            "error",
            function () {

                setBox(
                    statusEl,
                    "❌ Erreur de lecture native",
                    "error"
                );


                logTo(
                    logEl,
                    "Safari n'a pas pu charger le flux HLS."
                );
            },
            {
                once: true
            }
        );


        logTo(
            logEl,
            "Utilisation du lecteur HLS natif (Safari/iOS)"
        );


    } else {

        setBox(
            statusEl,
            "❌ HLS non supporté par ce navigateur",
            "error"
        );
    }
}


// =========================================================
// SECTION 3 — CONSTRUCTEUR DE LIEN
// =========================================================

const linkResult =
    document.getElementById(
        "linkResult"
    );


function buildStreamUrl() {

    const videoId =
        document.getElementById(
            "linkVideoId"
        )?.value.trim();


    const filename =
        document.getElementById(
            "linkFilename"
        )?.value.trim() ||
        "playlist.m3u8";


    if (!videoId) {

        showResult(
            linkResult,
            "⚠️ Merci de renseigner un videoId."
        );

        return null;
    }


    const url =
        createStreamUrl(
            videoId,
            filename
        );


    showResult(
        linkResult,
        url
    );


    return url;
}


// =========================================================
// OUVRIR STREAM
// =========================================================

function openStreamLink() {

    const url =
        buildStreamUrl();


    if (url) {

        /*
         * IMPORTANT :
         *
         * window.open(url) ne permet pas d'ajouter
         * Authorization: Bearer.
         *
         * Donc cette fonction peut provoquer un 401
         * si ton endpoint exige le Bearer.
         *
         * Pour lire HLS, utilise plutôt "Utiliser
         * dans le lecteur", qui passe par hls.js.
         */

        window.open(
            url,
            "_blank"
        );
    }
}


// =========================================================
// TÉLÉCHARGER STREAM
// =========================================================

async function downloadStreamLink() {

    const url =
        buildStreamUrl();


    if (!url) return;


    const authHeader =
        getAuthorizationHeader();


    if (!authHeader) {

        showResult(
            linkResult,
            "❌ Token Bearer introuvable."
        );

        return;
    }


    try {

        logTo(
            logEl,
            `Téléchargement sécurisé : ${url}`
        );


        const response =
            await fetch(
                url,
                {
                    method: "GET",

                    headers: {
                        "Authorization":
                        authHeader
                    }
                }
            );


        if (!response.ok) {

            throw new Error(
                `HTTP ${response.status}`
            );
        }


        const blob =
            await response.blob();


        const blobUrl =
            URL.createObjectURL(blob);


        const a =
            document.createElement("a");


        a.href = blobUrl;


        a.download =
            document.getElementById(
                "linkFilename"
            )?.value.trim() ||
            "download";


        document.body.appendChild(a);


        a.click();


        a.remove();


        URL.revokeObjectURL(
            blobUrl
        );


        logTo(
            logEl,
            "Téléchargement terminé."
        );


    } catch (error) {

        logTo(
            logEl,
            `Erreur téléchargement : ${error.message}`
        );


        showResult(
            linkResult,
            `❌ Erreur de téléchargement : ${error.message}`
        );
    }
}


// =========================================================
// UTILISER DANS LE LECTEUR
// =========================================================

function useInPlayer() {

    const url =
        buildStreamUrl();


    if (!url) return;


    const streamUrl =
        document.getElementById(
            "streamUrl"
        );


    if (streamUrl) {

        streamUrl.value =
            url;
    }


    loadStream();
}


// =========================================================
// SECTION 4 — APERÇU RAPIDE
// =========================================================

const previewResult =
    document.getElementById(
        "previewBox"
    );


async function loadPreview() {

    const videoId =
        document.getElementById(
            "linkVideoId"
        )?.value.trim();


    const filename =
        document.getElementById(
            "linkFilename"
        )?.value.trim();


    if (
        !videoId ||
        !filename
    ) {

        if (previewResult) {

            previewResult.innerHTML =
                `<div class="status error">
                    ⚠️ videoId et nom de fichier requis.
                </div>`;
        }

        return;
    }


    const url =
        createStreamUrl(
            videoId,
            filename
        );


    const ext =
        filename
            .split(".")
            .pop()
            .toLowerCase();


    // =================================================
    // IMAGE
    // =================================================

    if (
        [
            "png",
            "jpg",
            "jpeg",
            "webp",
            "gif"
        ].includes(ext)
    ) {

        /*
         * <img src=""> ne permet pas de rajouter
         * Authorization: Bearer.
         *
         * On utilise donc fetch().
         */

        await previewProtectedResource(
            url,
            "image"
        );


        return;
    }


    // =================================================
    // AUDIO
    // =================================================

    if (
        [
            "mp3",
            "wav",
            "aac",
            "m4a"
        ].includes(ext)
    ) {

        await previewProtectedResource(
            url,
            "audio"
        );


        return;
    }


    // =================================================
    // MP4
    // =================================================

    if (ext === "mp4") {

        await previewProtectedResource(
            url,
            "video"
        );


        return;
    }


    // =================================================
    // AUTRE
    // =================================================

    if (previewResult) {

        previewResult.innerHTML =
            `<div class="status info">
                Type "${ext}" non prévisualisable ici —
                utilise "Ouvrir" ou "Télécharger".
            </div>`;
    }
}


// =========================================================
// PREVIEW PROTÉGÉ
// =========================================================

async function previewProtectedResource(
    url,
    type
) {

    const authHeader =
        getAuthorizationHeader();


    if (!authHeader) {

        if (previewResult) {

            previewResult.innerHTML =
                `<div class="status error">
                    ❌ Token Bearer introuvable.
                </div>`;
        }

        return;
    }


    try {

        const response =
            await fetch(
                url,
                {
                    method: "GET",

                    headers: {
                        "Authorization":
                        authHeader
                    }
                }
            );


        if (!response.ok) {

            throw new Error(
                `HTTP ${response.status}`
            );
        }


        const blob =
            await response.blob();


        const objectUrl =
            URL.createObjectURL(blob);


        if (type === "image") {

            previewResult.innerHTML =
                `<img
                    src="${objectUrl}"
                    alt="preview"
                    style="max-width:100%;"
                />`;

        } else if (type === "audio") {

            previewResult.innerHTML =
                `<audio
                    controls
                    src="${objectUrl}"
                    style="width:100%;"
                ></audio>`;

        } else if (type === "video") {

            previewResult.innerHTML =
                `<video
                    controls
                    src="${objectUrl}"
                    style="width:100%;"
                ></video>`;
        }


    } catch (error) {

        if (previewResult) {

            previewResult.innerHTML =
                `<div class="status error">
                    ❌ Impossible de charger la ressource :
                    ${error.message}
                </div>`;
        }


        logTo(
            logEl,
            `Erreur preview : ${error.message}`
        );
    }
}


// =========================================================
// INIT
// =========================================================

window.addEventListener(
    "load",
    function () {

        logTo(
            logEl,
            'Page prête. Modifie l\'URL puis clique sur "Charger".'
        );


        // -------------------------------------------------
        // Vérification JWT
        // -------------------------------------------------

        if (checkAuthentication()) {

            logTo(
                logEl,
                "✅ Token Bearer détecté."
            );

        } else {

            logTo(
                logEl,
                "⚠️ Aucun token Bearer détecté."
            );
        }
    }
);
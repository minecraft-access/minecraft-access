// Expand-collapse toggles
for (const el of document.querySelectorAll(".toggle[aria-controls]")) {
    const controls = document.getElementById(el.getAttribute("aria-controls"));
    const initialState = controls.classList.contains("collapsed");
    el.classList.toggle("collapsed", initialState);
    el.setAttribute("aria-expanded", (!initialState).toString());

    function toggle(expanded?: boolean) {
        if (expanded === undefined) {
            expanded = controls.classList.contains("collapsed");
        }
        controls.classList.toggle("collapsed", !expanded);
        el.setAttribute("aria-expanded", (expanded).toString());
    }

    el.addEventListener("click", () => toggle());
    if (!(el instanceof HTMLButtonElement)) {
        el.addEventListener("keyup", (e: KeyboardEvent) => {
            if (e.code === "Enter" || e.code === "Space") {
                toggle();
            }
        });
    }

    for (const closer of document.querySelectorAll(`[data-closes=${controls.id}]`)) {
        if (closer === controls) {
            closer.addEventListener("click", (e) => e.target === closer && toggle(false));
        } else {
            closer.addEventListener("click", () => toggle(false));
        }
    }
}


// Table of contents highlighting
const headings = Array.from(document.querySelectorAll(".content h2, .content h3, .content h4")).reverse() as HTMLElement[];
const toc = document.getElementsByClassName("table-of-contents")[0];
document.addEventListener("scroll", () => {
    for (const el of toc.getElementsByTagName("a")) {
        el.classList.remove("active");
    }
    const heading = headings.filter(el => (el.offsetTop + el.parentElement.offsetTop - el.clientHeight*3) < document.scrollingElement.scrollTop)[0];
    if (heading === undefined) {
        return;
    }
    const tocLink = toc.querySelector(`a[href="#${heading.id}"]`) as HTMLElement;
    tocLink.classList.add("active");
    toc.scrollTo({top: tocLink.offsetTop - toc.clientHeight / 2});
});


// Timestamp localisation
for (const el of document.getElementsByTagName("time")) {
    el.textContent = new Date(el.dateTime).toLocaleString();
}


// Audio pitch adjustment
for (const el: HTMLAudioElement of document.querySelectorAll("audio[data-pitch]")) {
    el.playbackRate = Number(el.getAttribute("data-pitch"));
    el.preservesPitch = false;
}


// Media play buttons
for (const el: HTMLButtonElement of document.querySelectorAll("button[data-plays]")) {
    const media: HTMLMediaElement = <HTMLMediaElement>document.getElementById(el.getAttribute("data-plays"));
    el.addEventListener("click", async () => {
        media.currentTime = 0;
        await media.play();
    });
}

// External link icon
for (const el: HTMLAnchorElement of document.querySelectorAll("a[rel~=nofollow]")) {
    el.textContent = `${el.textContent} `
    const icon = document.createElement("span");
    icon.classList.add("icon");
    const iconInner = document.createElement("span");
    iconInner.classList.add("icon-inner");
    iconInner.classList.add("material-symbols-outlined");
    iconInner.ariaHidden = "true";
    iconInner.textContent = "arrow_outward";
    icon.appendChild(iconInner);
    const label = document.createElement("span");
    label.classList.add("sr-only");
    label.textContent = "(External)";
    icon.appendChild(label);
    el.appendChild(icon);
}

// Expand-collapse toggles
for (const el of document.querySelectorAll(".toggle[aria-controls]")) {
    const controls = document.getElementById(el.getAttribute("aria-controls"));
    const initialState = controls.classList.contains("collapsed");
    el.classList.toggle("collapsed", initialState);
    el.setAttribute("aria-expanded", (!initialState).toString());

    function toggle() {
        const collapsed = controls.classList.toggle("collapsed");
        el.setAttribute("aria-expanded", (!collapsed).toString());
    }

    el.addEventListener("click", toggle);
    el.addEventListener("keyup", (e: KeyboardEvent) => {
        if (e.code === "Enter" || e.code === "Space") {
            toggle();
        }
    });
}


// Table of contents highlighting
const headings = Array.from(document.querySelectorAll(".content h2, .content h3, .content h4")).reverse() as HTMLElement[];
const toc = document.getElementsByClassName("table-of-contents")[0];
const content = document.getElementsByClassName("content")[0];
content.addEventListener("scroll", () => {
    for (const el of toc.getElementsByTagName("a")) {
        el.classList.remove("active");
    }
    const heading = headings.filter(el => (el.offsetTop - el.clientHeight * 3) < content.scrollTop)[0];
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

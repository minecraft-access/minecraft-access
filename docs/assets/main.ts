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

const headings = Array.from(document.querySelectorAll(".content h2, .content h3, .content h4")).reverse() as HTMLElement[];
const toc = document.getElementsByClassName("table-of-contents")[0];
const content = document.getElementsByClassName("content")[0];
function onScroll() {
    for (const el of toc.getElementsByTagName("a")) {
        el.classList.remove("active");
    }
    const heading = headings.filter(el => (el.offsetTop - el.clientHeight*3) < content.scrollTop)[0];
    if (heading === undefined) {
        return;
    }
    const tocLink = toc.querySelector(`a[href="#${heading.id}"]`) as HTMLElement;
    tocLink.classList.add("active");
    toc.scrollTo({top: tocLink.offsetTop - toc.clientHeight/2});
}
content.addEventListener("scroll", onScroll);
onScroll();

for (const el of document.getElementsByTagName("time")) {
    el.textContent = new Date(el.dateTime).toLocaleString();
}

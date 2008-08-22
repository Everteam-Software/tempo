
function toggleContentState(imageId, contentId) {
	var image = document.getElementById(imageId);
	var content = document.getElementById(contentId);
	
	content.style.display = (content.style.display == "none") ? "block" : "none";
	
	if (content.style.display == "none") {
		image.src = "images/section_close.gif";
	} else {
		image.src = "images/section_open.gif";
	}
}

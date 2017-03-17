var initial = document.body;

function clickResursively(element) {
    if (element.hasAttribute != undefined && element.getAttribute("id") == "term-go" && element.getAttribute("id") == "startDatepicker") {

    } else {
        if(element.click != undefined) {
            element.click();
        }
        var children = element.childNodes;
        for(var i = 0; i < children.length; i ++) {
            (function (index) {
                clickResursively(children[index]);
            })(i);
        }
    }
}

clickResursively(initial);
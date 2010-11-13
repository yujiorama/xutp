var bodyStyle = {
    width: '100%',
    height: '100%',
    padding: '0',
    margin: '0',
    overflow: 'hidden',
    backgroundColor: 'gray'
};

s6.css('html', bodyStyle);
s6.css('body', bodyStyle);

var pr;
s6.attach(s6, 'ready', function ready() {
    var html = document.documentElement;
    var height = html.offsetHeight;
    var width = html.offsetWidth;
    var top = 0;
    var left = 0;

    if (height / width < 0.75) {
        var originalWidth = width;
        width = height / 0.75;
        left = (originalWidth - width) / 2 + 'px';
    }
    else {
        var originalHeight = height;
        height = width * 0.75;
        top = (originalHeight - height) / 2 + 'px';
    }

    var result;
    if (result = document.cookie.match(/page=(\d+)/)) {
        var startIndex = +result[1];
    }
    
    pr = new s6.Presentation({ element: document.body, width: width, height: height, startIndex: startIndex });
    pr.style.left = left;
    pr.style.top = top;
    pr.start();

    var indexNoOutline = false;

    pr.funcPages.index.attachPage('click', function(i, element, wrapper) {
        indexNoOutline = true;
        setTimeout(function() {
            indexNoOutline =false
        }, 1000);
        wrapper.style.background = '';
        pr.go(i);
    });

    pr.funcPages.index.attachPage('mouseover', function(i, element, wrapper) {
        if (indexNoOutline) return;
        wrapper.className += ' selected';
    });
    
    pr.funcPages.index.attachPage('mouseout', function(i, element, wrapper) {
        wrapper.className = 'wrapper';
    });

    try {
        var isIframe = !(window.parent == window);
    } catch(e) {
        isIframe = true;
    }

    s6.attach(document,   'keypress Right', 'step', 0, pr);
    s6.attach(document,   'keypress Left',  'prev', 0, pr);
    s6.attach(document,   'keypress i',    function() { pr.go('index') });
    s6.attach(document,   'keypress r',    function() { window.location.href='./index.html' });
    //    s6.attach(document,   'keypress Down',  'back', 0, pr);
});

// Test
setInterval(function() {
    document.cookie = 'page=' + pr.index;
}, 1000);




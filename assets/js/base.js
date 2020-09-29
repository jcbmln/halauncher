(function($) {

    'use strict';

    $(function() {
        $('[data-toggle="tooltip"]').tooltip();
        $('[data-toggle="popover"]').popover();

        $('.popover-dismiss').popover({
            trigger: 'focus'
        })
    });


    function bottomPos(element) {
        return element.offset().top + element.outerHeight();
    }

    // Bootstrap Fixed Header
    $(function() {
        const cover = $('.cover');
        const navbar = $('.navbar')
        if (!cover.length) {
            return
        }

        const threshold = Math.ceil(navbar.outerHeight());

        $(window).on('scroll', function() {
            const coverOffset = bottomPos(cover);
            const navbarOffset = navbar.offset().top;
            if ((coverOffset - navbarOffset) < threshold) {
                navbar.addClass('scrolled');
            } else {
                navbar.removeClass('scrolled');
            }
        });
    });


}(jQuery));

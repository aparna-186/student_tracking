(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        var menuButton = document.querySelector('[data-menu-toggle]');
        var sidebar = document.querySelector('.sidebar');
        if (menuButton && sidebar) {
            menuButton.addEventListener('click', function () {
                var open = document.body.classList.toggle('nav-open');
                menuButton.setAttribute('aria-expanded', String(open));
            });
            document.addEventListener('keydown', function (event) {
                if (event.key === 'Escape' && document.body.classList.contains('nav-open')) {
                    document.body.classList.remove('nav-open');
                    menuButton.setAttribute('aria-expanded', 'false');
                    menuButton.focus();
                }
            });
            document.addEventListener('click', function (event) {
                if (document.body.classList.contains('nav-open') && !sidebar.contains(event.target) && !menuButton.contains(event.target)) {
                    document.body.classList.remove('nav-open');
                    menuButton.setAttribute('aria-expanded', 'false');
                }
            });
        }

        document.querySelectorAll('[data-password-toggle]').forEach(function (button) {
            button.addEventListener('click', function () {
                var input = document.getElementById(button.getAttribute('aria-controls'));
                if (!input) return;
                var visible = input.type === 'password';
                input.type = visible ? 'text' : 'password';
                button.textContent = visible ? 'Hide' : 'Show';
                button.setAttribute('aria-pressed', String(visible));
            });
        });

        document.querySelectorAll('[data-dismissible]').forEach(function (alert) {
            var button = alert.querySelector('[data-dismiss]');
            if (button) button.addEventListener('click', function () { alert.remove(); });
        });

        document.querySelectorAll('form[data-confirm]').forEach(function (form) {
            form.addEventListener('submit', function (event) {
                if (!window.confirm(form.getAttribute('data-confirm'))) event.preventDefault();
            });
        });

        document.querySelectorAll('form[data-submitting]').forEach(function (form) {
            form.addEventListener('submit', function () {
                var button = form.querySelector('button[type="submit"]');
                if (button && form.checkValidity()) {
                    button.disabled = true;
                    button.setAttribute('aria-busy', 'true');
                }
            });
        });
    });
}());

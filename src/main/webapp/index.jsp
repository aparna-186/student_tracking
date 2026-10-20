<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="SkillSprint helps students organize internship applications, coding practice, certificates, and career goals.">
    <title>SkillSprint — Make your next move</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/skillsprint.css">
</head>
<body class="landing">
<header class="public-header">
    <a class="brand" href="<%= request.getContextPath() %>/"><span class="brand-mark" aria-hidden="true">S</span><span>SkillSprint</span></a>
    <nav class="public-nav" aria-label="Main navigation"><a href="#features">Features</a><a href="<%= request.getContextPath() %>/login">Sign in</a><a class="button" href="<%= request.getContextPath() %>/register">Create account</a></nav>
</header>
<main>
    <section class="hero">
        <div class="hero-copy">
            <span class="eyebrow"><span class="eyebrow-dot"></span> Your career, in motion</span>
            <h1>Make your next career move <span>count.</span></h1>
            <p>One calm, clear workspace for the internships you pursue, the skills you build, and the goals that move you forward.</p>
            <div class="hero-actions"><a class="button" href="<%= request.getContextPath() %>/register">Get started for free</a><a class="button secondary" href="<%= request.getContextPath() %>/login">I already have an account</a></div>
        </div>
        <div class="hero-visual" aria-label="Preview of the SkillSprint career dashboard">
            <div class="preview-window"><div class="preview-top"><i></i><i></i><i></i></div><div class="preview-layout"><div class="preview-side"><span></span><span></span><span></span><span></span><span></span></div><div class="preview-main"><div class="preview-title"></div><div class="preview-metrics"><div class="preview-metric"></div><div class="preview-metric"></div><div class="preview-metric"></div></div><div class="preview-list"><div class="preview-row"><span></span></div><div class="preview-row"><span></span></div><div class="preview-row"><span></span></div></div></div></div></div>
            <div class="floating-note">Your next step <span>✓</span></div>
        </div>
    </section>
    <section class="features" id="features"><div class="section-inner"><div class="section-heading"><span class="eyebrow">Built for your journey</span><h2>Keep the important things moving</h2><p>Bring your career preparation into one place, so progress is easier to see and easier to keep making.</p></div>
        <div class="feature-grid">
            <article class="feature-card"><span class="shortcut-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><rect x="4" y="4" width="16" height="17" rx="2"/><path d="M8 4V2m8 2V2M8 10h8M8 14h6"/></svg></span><h3>Internship applications</h3><p>Keep company details, application dates, notes, and progress together from first application to final decision.</p></article>
            <article class="feature-card"><span class="shortcut-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M8 5 3 12l5 7m8-14 5 7-5 7m-2-16-4 20"/></svg></span><h3>Coding progress</h3><p>Record practice across your favorite platforms and track problem difficulty and status at a glance.</p></article>
            <article class="feature-card"><span class="shortcut-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="m8 12 2.5 2.5L16 9"/></svg></span><h3>Goals and certificates</h3><p>Give your learning goals a deadline, and keep certificates and credential links ready to share.</p></article>
        </div>
    </div></section>
    <section class="final-cta"><h2>A little progress adds up.</h2><p>Start organizing your next career step today.</p><a class="button" href="<%= request.getContextPath() %>/register">Create your account</a></section>
</main>
<footer class="public-footer"><a class="brand" href="<%= request.getContextPath() %>/"><span class="brand-mark" aria-hidden="true">S</span><span>SkillSprint</span></a><span>Made for students building what comes next.</span></footer>
</body>
</html>

/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{vue,js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                primary: "var(--primary)",
                "primary-hover": "var(--primary-hover)",
                secondary: "var(--secondary)",
                success: "var(--success)",
                warning: "var(--warning)",
                danger: "var(--danger)",
                background: "var(--background)",
                surface: "var(--surface)",
                "text-main": "var(--text-main)",
                "text-muted": "var(--text-muted)",
            },
            borderRadius: {
                xl: "16px",
                '2xl': "24px",
            },
            boxShadow: {
                premium: "0 20px 25px -5px rgb(0 0 0 / 0.05), 0 8px 10px -6px rgb(0 0 0 / 0.05)",
            }
        },
    },
    plugins: [],
}

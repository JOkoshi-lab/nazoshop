INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'ニンテンドー3DS LL', '本体', '任天堂', 18800, 10, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'ニンテンドー3DS LL'
      AND company = '任天堂'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'Wii U プレミアムセット', '本体', '任天堂', 31500, 10, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'Wii U プレミアムセット'
      AND company = '任天堂'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'Nintendo Switch', '本体', '任天堂', 37980, 10, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'Nintendo Switch'
      AND company = '任天堂'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'Nintendo Switch Lite', '本体', '任天堂', 21978, 10, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'Nintendo Switch Lite'
      AND company = '任天堂'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'Nintendo Switch 2', '本体', '任天堂', 49980, 10, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'Nintendo Switch 2'
      AND company = '任天堂'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'Switch Proコントローラー', '付属品', '任天堂', 7678, 10, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'Switch Proコントローラー'
      AND company = '任天堂'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'Joy-Con(L)/(R) ネオンブルー・ネオンレッド',
    '付属品',
    '任天堂',
    8228,
    10,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'Joy-Con(L)/(R) ネオンブルー・ネオンレッド'
      AND company = '任天堂'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'PlayStation Vita', '本体', 'ソニー', 19980, 10, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'PlayStation Vita'
      AND company = 'ソニー'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'PlayStation 4', '本体', 'ソニー', 32978, 10, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'PlayStation 4'
      AND company = 'ソニー'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'PlayStation 5', '本体', 'ソニー', 66980, 10, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'PlayStation 5'
      AND company = 'ソニー'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'DUALSHOCK 4 ワイヤレスコントローラー',
    '付属品',
    'ソニー',
    6578,
    10,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'DUALSHOCK 4 ワイヤレスコントローラー'
      AND company = 'ソニー'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'DualSense ワイヤレスコントローラー',
    '付属品',
    'ソニー',
    9480,
    10,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'DualSense ワイヤレスコントローラー'
      AND company = 'ソニー'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'PlayStation VR2', '付属品', 'ソニー', 74980, 10, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'PlayStation VR2'
      AND company = 'ソニー'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'Xbox One', '本体', 'マイクロソフト', 43178, 10, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'Xbox One'
      AND company = 'マイクロソフト'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'Xbox Series S', '本体', 'マイクロソフト', 37978, 10, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'Xbox Series S'
      AND company = 'マイクロソフト'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'Xbox Series X', '本体', 'マイクロソフト', 59980, 10, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'Xbox Series X'
      AND company = 'マイクロソフト'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'Xbox ワイヤレス コントローラー',
    '付属品',
    'マイクロソフト',
    6578,
    10,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'Xbox ワイヤレス コントローラー'
      AND company = 'マイクロソフト'
);

INSERT INTO items (
    name, category, company, price, stock, active
)
SELECT
    'Xbox Elite ワイヤレス コントローラー シリーズ 2',
    '付属品',
    'マイクロソフト',
    21978,
    10,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM items
    WHERE name = 'Xbox Elite ワイヤレス コントローラー シリーズ 2'
      AND company = 'マイクロソフト'
);
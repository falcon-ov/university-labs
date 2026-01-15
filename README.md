## How to download only one lab or course

The repository contains **all lab works**, but you **don’t have to clone everything**. Below are several ways.

---

### Download one lab (using sparse-checkout)

Suitable if you need **one specific folder**.

```bash
git clone --no-checkout https://github.com/falcon-ov/university-labs.git
cd university-labs

git sparse-checkout init --cone
git sparse-checkout set year-3-design-soft/design-soft-lab1

git checkout
```

The working directory will contain **only this lab**.

---

### Download all labs of one course

For example, all design soft labs of the third year:

```bash
git clone --no-checkout https://github.com/falcon-ov/university-labs.git
cd university-labs

git sparse-checkout init --cone
git sparse-checkout set year-3-design-soft

git checkout
```

* `sparse-checkout` — the official Git mechanism
* You can change the folder at any time:

```bash
git sparse-checkout set year-3-ai
```

* To restore all files:

```bash
git sparse-checkout disable
```

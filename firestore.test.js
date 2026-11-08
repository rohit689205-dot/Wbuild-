const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_UID = "admin_789";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: can read categories and published builds, but cannot read draft builds", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("categories").doc("farm").set({
      name: "Farm",
      slug: "farm",
      sortOrder: 1,
      createdAt: new Date(),
      updatedAt: new Date()
    });
    await context.firestore().collection("builds").doc("pub_1").set({
      title: "Crop Farm",
      categoryId: "farm",
      difficulty: "Easy",
      edition: "Both",
      status: "published",
      totalSteps: 5,
      createdBy: "builder_1",
      createdAt: new Date(),
      updatedAt: new Date()
    });
    await context.firestore().collection("builds").doc("draft_2").set({
      title: "Unfinished Farm",
      categoryId: "farm",
      difficulty: "Easy",
      edition: "Both",
      status: "draft",
      totalSteps: 2,
      createdBy: "builder_1",
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertSucceeds(unauthDb.collection("categories").get());
  await assertSucceeds(unauthDb.collection("builds").doc("pub_1").get());
  await assertFails(unauthDb.collection("builds").doc("draft_2").get());
});

test("Authenticated user: can read published builds and categories", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("categories").doc("houses").set({
      name: "Houses",
      slug: "houses",
      sortOrder: 1,
      createdAt: new Date(),
      updatedAt: new Date()
    });
    await context.firestore().collection("builds").doc("build_1").set({
      title: "Cozy Cottage",
      categoryId: "houses",
      difficulty: "Easy",
      edition: "Both",
      status: "published",
      totalSteps: 10,
      createdBy: "builder_1",
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("categories").doc("houses").get());
  await assertSucceeds(aliceDb.collection("builds").doc("build_1").get());
});

test("Authenticated user: cannot read draft builds unless admin", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("builds").doc("draft_1").set({
      title: "Secret Base",
      categoryId: "houses",
      difficulty: "Hard",
      edition: "Java",
      status: "draft",
      totalSteps: 5,
      createdBy: "builder_1",
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("builds").doc("draft_1").get());
});

test("Non-admin user: cannot create builds or categories", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("categories").doc("castles").set({
      name: "Castles",
      slug: "castles",
      sortOrder: 2,
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );
  await assertFails(
    aliceDb.collection("builds").doc("build_2").set({
      title: "Medieval Castle",
      categoryId: "castles",
      difficulty: "Expert",
      edition: "Both",
      status: "published",
      totalSteps: 50,
      createdBy: ALICE_UID,
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );
});

test("Admin user: can create build and category", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("admins").doc(ADMIN_UID).set({
      email: "admin@wbuilds.com",
      createdAt: new Date()
    });
  });

  const adminDb = testEnv.authenticatedContext(ADMIN_UID).firestore();
  await assertSucceeds(
    adminDb.collection("categories").doc("castles").set({
      name: "Castles",
      slug: "castles",
      sortOrder: 2,
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );
  await assertSucceeds(
    adminDb.collection("builds").doc("build_2").set({
      title: "Medieval Castle",
      categoryId: "castles",
      difficulty: "Expert",
      edition: "Both",
      status: "published",
      totalSteps: 50,
      createdBy: ADMIN_UID,
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );
});

test("User isolation: Alice cannot read or write Bob's favorites or progress", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  // Alice can manage her own favorites
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("favorites").doc("build_1").set({
      buildId: "build_1",
      buildTitle: "Cozy Cottage",
      createdAt: new Date()
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("favorites").doc("build_1").get()
  );

  // Alice cannot read or write Bob's favorites
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("favorites").doc("build_1").set({
      buildId: "build_1",
      buildTitle: "Cozy Cottage",
      createdAt: new Date()
    })
  );
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("favorites").doc("build_1").get()
  );
});
